import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface TenantRequestEvent {
  requestId: number;
  requestedByUserId: number;
  status: 'CREATED' | 'APPROVED' | 'REJECTED';
  email: string;
  firstName: string;
  lastName: string;
  timestamp?: string;
}

interface UseWebSocketOptions {
  onMessage: (event: TenantRequestEvent) => void;
  onConnect?: () => void;
  onError?: (error: any) => void;
}

export const useWebSocket = ({ onMessage, onConnect, onError }: UseWebSocketOptions) => {
  const clientRef = useRef<Client | null>(null);
  const callbacksRef = useRef({ onMessage, onConnect, onError });

  // Update callbacks ref when they change
  useEffect(() => {
    callbacksRef.current = { onMessage, onConnect, onError };
  }, [onMessage, onConnect, onError]);

  useEffect(() => {
    // WebSocket endpoint - connect directly to Dashboard Service
    // Note: WebSocket connections typically work better directly to service
    const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8084/ws';
    
    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl) as any,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('WebSocket Connected');
        if (callbacksRef.current.onConnect) {
          callbacksRef.current.onConnect();
        }
        
        // Subscribe to notifications topic
        client.subscribe('/topic/notifications', (message) => {
          try {
            const event: TenantRequestEvent = JSON.parse(message.body);
            console.log('Received WebSocket notification:', event);
            callbacksRef.current.onMessage(event);
          } catch (error) {
            console.error('Error parsing WebSocket message:', error);
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        if (callbacksRef.current.onError) {
          callbacksRef.current.onError(frame);
        }
      },
      onWebSocketError: (error) => {
        console.error('WebSocket error:', error);
        if (callbacksRef.current.onError) {
          callbacksRef.current.onError(error);
        }
      },
      onDisconnect: () => {
        console.log('WebSocket Disconnected');
      },
    });

    clientRef.current = client;
    client.activate();

    // Cleanup on unmount
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
    };
  }, []); // Empty dependency array - only connect once

  return clientRef.current;
};

