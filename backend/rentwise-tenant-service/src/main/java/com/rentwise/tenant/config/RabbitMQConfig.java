package com.rentwise.tenant.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String QUEUE_TENANT_REQUEST = "tenant.request.queue";
    public static final String EXCHANGE_TENANT_REQUEST = "tenant.request.exchange";
    public static final String ROUTING_KEY_TENANT_REQUEST = "tenant.request.routing";
    
    @Bean
    public Queue tenantRequestQueue() {
        return QueueBuilder.durable(QUEUE_TENANT_REQUEST).build();
    }
    
    @Bean
    public TopicExchange tenantRequestExchange() {
        return new TopicExchange(EXCHANGE_TENANT_REQUEST);
    }
    
    @Bean
    public Binding tenantRequestBinding() {
        return BindingBuilder
            .bind(tenantRequestQueue())
            .to(tenantRequestExchange())
            .with(ROUTING_KEY_TENANT_REQUEST);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

