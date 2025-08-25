package com.rentwise.dashboard.controller;

import com.rentwise.dashboard.client.UserServiceClient;
import com.rentwise.dashboard.dto.TenantRequestDTO;
import com.rentwise.dashboard.dto.UserDTO;
import com.rentwise.dashboard.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private static final String SERVICE_NAME = "rentwise-dashboard-service";
    
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @GetMapping
    public String dashboard(@RequestParam(required = false) String username, 
                           HttpSession session, 
                           Model model) {
        logger.info("[{}] [DashboardController] [dashboard] START - Username: {}", SERVICE_NAME, username);
        
        try {
            // If username provided, fetch user and store in session
            if (username != null && !username.isEmpty()) {
                UserDTO user = userServiceClient.getUserByUsername(username);
                if (user != null) {
                    session.setAttribute("user", user);
                    logger.info("[{}] [DashboardController] [dashboard] User loaded: {} with role: {}", 
                            SERVICE_NAME, user.getUsername(), user.getRole());
                    
                    // Route based on role
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        return "redirect:/dashboard/admin";
                    } else {
                        return "redirect:/dashboard/user";
                    }
                } else {
                    logger.warn("[{}] [DashboardController] [dashboard] User not found: {}", SERVICE_NAME, username);
                    return "redirect:http://localhost:8081/login?error";
                }
            }
            
            // Check if user exists in session
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user != null) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    return "redirect:/dashboard/admin";
                } else {
                    return "redirect:/dashboard/user";
                }
            }
            
            // No user info, redirect to login
            logger.warn("[{}] [DashboardController] [dashboard] No user info, redirecting to login", SERVICE_NAME);
            return "redirect:http://localhost:8081/login";
            
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [dashboard] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            return "redirect:http://localhost:8081/login?error";
        }
    }
    
    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        logger.info("[{}] [DashboardController] [adminDashboard] START", SERVICE_NAME);
        try {
            // Check if user is in session and is ADMIN
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                logger.warn("[{}] [DashboardController] [adminDashboard] Unauthorized access attempt", SERVICE_NAME);
                return "redirect:http://localhost:8081/login";
            }
            
            List<com.rentwise.dashboard.dto.PropertyDTO> properties = dashboardService.getAllProperties();
            List<com.rentwise.dashboard.dto.TenantDTO> tenants = dashboardService.getAllTenants();
            List<TenantRequestDTO> pendingRequests = dashboardService.getPendingTenantRequests();
            
            model.addAttribute("properties", properties);
            model.addAttribute("tenants", tenants);
            model.addAttribute("pendingRequests", pendingRequests);
            model.addAttribute("user", user);
            
            logger.info("[{}] [DashboardController] [adminDashboard] SUCCESS - Loaded admin dashboard", SERVICE_NAME);
            return "dashboard/admin";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [adminDashboard] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "dashboard/admin";
        }
    }
    
    @GetMapping("/user")
    public String userDashboard(HttpSession session, Model model) {
        logger.info("[{}] [DashboardController] [userDashboard] START", SERVICE_NAME);
        try {
            // Get user from session
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                logger.warn("[{}] [DashboardController] [userDashboard] No user in session, redirecting to login", SERVICE_NAME);
                return "redirect:http://localhost:8081/login";
            }
            
            Long userId = user.getId();
            List<com.rentwise.dashboard.dto.TenantDTO> myTenants = dashboardService.getTenantsByUserId(userId);
            List<TenantRequestDTO> myRequests = dashboardService.getTenantRequestsByUser(userId);
            List<com.rentwise.dashboard.dto.PropertyDTO> myProperties = dashboardService.getPropertiesByUserId(userId);
            
            model.addAttribute("tenants", myTenants);
            model.addAttribute("requests", myRequests);
            model.addAttribute("properties", myProperties);
            model.addAttribute("user", user);
            
            logger.info("[{}] [DashboardController] [userDashboard] SUCCESS - Loaded user dashboard", SERVICE_NAME);
            return "dashboard/user";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [userDashboard] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "dashboard/user";
        }
    }
    
    // Tenant Request endpoints for Users
    @GetMapping("/user/tenants/request")
    public String showTenantRequestForm(Model model) {
        logger.info("[{}] [DashboardController] [showTenantRequestForm] START", SERVICE_NAME);
        model.addAttribute("tenantRequest", new TenantRequestDTO());
        return "dashboard/tenant-request-form";
    }
    
    @PostMapping("/user/tenants/request")
    public String submitTenantRequest(@ModelAttribute TenantRequestDTO request, 
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        logger.info("[{}] [DashboardController] [submitTenantRequest] START - Email: {}", 
                SERVICE_NAME, request.getEmail());
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return "redirect:http://localhost:8081/login";
            }
            
            Long userId = user.getId();
            request.setRequestedByUserId(userId);
            request.setStatus("PENDING");
            
            dashboardService.createTenantRequest(request);
            redirectAttributes.addFlashAttribute("success", "Tenant request submitted successfully!");
            logger.info("[{}] [DashboardController] [submitTenantRequest] SUCCESS", SERVICE_NAME);
            return "redirect:/dashboard/user";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [submitTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to submit request: " + e.getMessage());
            return "redirect:/dashboard/user/tenants/request";
        }
    }
    
    // Admin approval endpoints
    @PostMapping("/admin/tenants/requests/{id}/approve")
    public String approveTenantRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("[{}] [DashboardController] [approveTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            dashboardService.approveTenantRequest(id);
            redirectAttributes.addFlashAttribute("success", "Tenant request approved successfully!");
            logger.info("[{}] [DashboardController] [approveTenantRequest] SUCCESS", SERVICE_NAME);
            return "redirect:/dashboard/admin";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [approveTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to approve request: " + e.getMessage());
            return "redirect:/dashboard/admin";
        }
    }
    
    @PostMapping("/admin/tenants/requests/{id}/reject")
    public String rejectTenantRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("[{}] [DashboardController] [rejectTenantRequest] START - Request ID: {}", SERVICE_NAME, id);
        try {
            dashboardService.rejectTenantRequest(id);
            redirectAttributes.addFlashAttribute("success", "Tenant request rejected.");
            logger.info("[{}] [DashboardController] [rejectTenantRequest] SUCCESS", SERVICE_NAME);
            return "redirect:/dashboard/admin";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [rejectTenantRequest] ERROR - {}", 
                    SERVICE_NAME, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to reject request: " + e.getMessage());
            return "redirect:/dashboard/admin";
        }
    }
    
    @PostMapping("/admin/tenants/{id}/assign-property")
    public String assignPropertyToTenant(@PathVariable Long id,
                                        @RequestParam Long propertyId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        logger.info("[{}] [DashboardController] [assignPropertyToTenant] START - Tenant ID: {}, Property ID: {}", 
                SERVICE_NAME, id, propertyId);
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                logger.warn("[{}] [DashboardController] [assignPropertyToTenant] Unauthorized access attempt", SERVICE_NAME);
                redirectAttributes.addFlashAttribute("error", "Unauthorized to assign properties.");
                return "redirect:/dashboard/admin";
            }
            
            dashboardService.assignPropertyToTenant(id, propertyId);
            redirectAttributes.addFlashAttribute("success", "Property assigned to tenant successfully!");
            logger.info("[{}] [DashboardController] [assignPropertyToTenant] SUCCESS", SERVICE_NAME);
            return "redirect:/dashboard/admin";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [assignPropertyToTenant] ERROR - {}", SERVICE_NAME, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to assign property: " + e.getMessage());
            return "redirect:/dashboard/admin";
        }
    }
    
    @GetMapping("/home")
    public String home(HttpSession session) {
        logger.info("[{}] [DashboardController] [home] START - Redirecting to appropriate dashboard", SERVICE_NAME);
        
        // Check if user exists in session
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user != null) {
            // Route based on role
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/dashboard/admin";
            } else {
                return "redirect:/dashboard/user";
            }
        }
        
        // No user in session, redirect to login
        logger.warn("[{}] [DashboardController] [home] No user in session, redirecting to login", SERVICE_NAME);
        return "redirect:http://localhost:8081/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("[{}] [DashboardController] [logout] START - Logging out user", SERVICE_NAME);
        session.invalidate();
        return "redirect:http://localhost:8081/login?logout";
    }
    
    @GetMapping("/admin/pending-requests")
    public String getPendingRequestsFragment(HttpSession session, Model model) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                return "dashboard/fragments/empty :: pending-requests";
            }
            List<TenantRequestDTO> pendingRequests = dashboardService.getPendingTenantRequests();
            model.addAttribute("pendingRequests", pendingRequests);
            return "dashboard/fragments/pending-requests :: pending-requests";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [getPendingRequestsFragment] ERROR - {}", SERVICE_NAME, e.getMessage());
            return "dashboard/fragments/empty :: pending-requests";
        }
    }
    
    @GetMapping("/admin/tenants")
    public String getTenantsFragment(HttpSession session, Model model) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                return "dashboard/fragments/empty :: tenants";
            }
            List<com.rentwise.dashboard.dto.TenantDTO> tenants = dashboardService.getAllTenants();
            List<com.rentwise.dashboard.dto.PropertyDTO> properties = dashboardService.getAllProperties();
            model.addAttribute("tenants", tenants);
            model.addAttribute("properties", properties);
            return "dashboard/fragments/tenants :: tenants";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [getTenantsFragment] ERROR - {}", SERVICE_NAME, e.getMessage());
            return "dashboard/fragments/empty :: tenants";
        }
    }
    
    @GetMapping("/user/roommates")
    public String getRoommatesFragment(HttpSession session, Model model) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return "dashboard/fragments/empty :: roommates";
            }
            Long userId = user.getId();
            List<com.rentwise.dashboard.dto.TenantDTO> myTenants = dashboardService.getTenantsByUserId(userId);
            model.addAttribute("tenants", myTenants);
            return "dashboard/fragments/roommates :: roommates";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [getRoommatesFragment] ERROR - {}", SERVICE_NAME, e.getMessage());
            return "dashboard/fragments/empty :: roommates";
        }
    }
    
    @GetMapping("/user/requests")
    public String getRequestsFragment(HttpSession session, Model model) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return "dashboard/fragments/empty :: requests";
            }
            Long userId = user.getId();
            List<TenantRequestDTO> myRequests = dashboardService.getTenantRequestsByUser(userId);
            model.addAttribute("requests", myRequests);
            return "dashboard/fragments/requests :: requests";
        } catch (Exception e) {
            logger.error("[{}] [DashboardController] [getRequestsFragment] ERROR - {}", SERVICE_NAME, e.getMessage());
            return "dashboard/fragments/empty :: requests";
        }
    }
}

