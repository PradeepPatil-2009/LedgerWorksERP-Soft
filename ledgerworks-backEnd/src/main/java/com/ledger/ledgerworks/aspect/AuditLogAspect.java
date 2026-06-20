package com.ledger.ledgerworks.aspect;

import com.ledger.ledgerworks.service.AuditLogService;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Cross-cutting audit capture. Wraps every public method of a
 * {@code @RestController} that is mapped to POST / PUT / DELETE and records an
 * {@link com.ledger.ledgerworks.entity.AuditLog} entry after the method returns
 * successfully. Best-effort only: this aspect never breaks the request.
 */
@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // Any public method inside a class annotated with @RestController.
    @AfterReturning(
            "within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void afterRestControllerMethod(JoinPoint joinPoint) {

        try {

            MethodSignature signature =
                    (MethodSignature) joinPoint.getSignature();

            Method method = signature.getMethod();

            String action = resolveAction(method);

            // Only audit write operations (POST / PUT / DELETE).
            if (action == null) {
                return;
            }

            String entityType =
                    joinPoint.getTarget().getClass().getSimpleName();

            String detail = method.getName() + "()";

            auditLogService.record(action, entityType, detail);

        } catch (Exception ignored) {
            // Never let auditing interfere with the actual request.
        }
    }

    // Map the HTTP-mapping annotation on the method to an audit action.
    private String resolveAction(Method method) {

        if (method.isAnnotationPresent(
                org.springframework.web.bind.annotation.PostMapping.class)) {
            return "CREATE";
        }

        if (method.isAnnotationPresent(
                org.springframework.web.bind.annotation.PutMapping.class)) {
            return "UPDATE";
        }

        if (method.isAnnotationPresent(
                org.springframework.web.bind.annotation.DeleteMapping.class)) {
            return "DELETE";
        }

        // Generic @RequestMapping with an explicit write method.
        org.springframework.web.bind.annotation.RequestMapping requestMapping =
                method.getAnnotation(
                        org.springframework.web.bind.annotation.RequestMapping.class);

        if (requestMapping != null) {

            for (org.springframework.web.bind.annotation.RequestMethod rm
                    : requestMapping.method()) {

                switch (rm) {
                    case POST:
                        return "CREATE";
                    case PUT:
                    case PATCH:
                        return "UPDATE";
                    case DELETE:
                        return "DELETE";
                    default:
                        // fall through to next mapped method
                }
            }
        }

        return null;
    }
}
