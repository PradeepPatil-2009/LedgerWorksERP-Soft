import {
  createContext,
  useCallback,
  useContext,
  useRef,
  useState,
} from "react";

// =====================================================
// Reusable toast / notification system.
//
// Usage:
//   const toast = useToast();
//   toast.success("Saved");
//   toast.error("Something went wrong");
//   toast.info("Heads up");
//
// Wrap the app once with <ToastProvider> (done in App.js).
// =====================================================

const ToastContext = createContext(null);

export function useToast() {
  const ctx = useContext(ToastContext);

  // Fallback so calls never crash even if the provider is missing.
  if (!ctx) {
    return {
      show: () => {},
      success: () => {},
      error: () => {},
      info: () => {},
    };
  }

  return ctx;
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const idRef = useRef(0);

  const remove = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const show = useCallback(
    (message, type = "info", duration = 3500) => {
      if (message == null || message === "") return;

      const id = ++idRef.current;

      setToasts((prev) => [
        ...prev,
        { id, message: String(message), type },
      ]);

      if (duration > 0) {
        setTimeout(() => remove(id), duration);
      }

      return id;
    },
    [remove]
  );

  const success = useCallback(
    (message, duration) => show(message, "success", duration),
    [show]
  );

  const error = useCallback(
    (message, duration) => show(message, "error", duration),
    [show]
  );

  const info = useCallback(
    (message, duration) => show(message, "info", duration),
    [show]
  );

  const value = { show, success, error, info };

  return (
    <ToastContext.Provider value={value}>
      {children}

      <div className="toast-container" aria-live="polite" aria-atomic="true">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`toast toast-${t.type}`}
            role="alert"
            onClick={() => remove(t.id)}
          >
            <span className="toast-message">{t.message}</span>

            <button
              type="button"
              className="toast-close"
              aria-label="Dismiss notification"
              onClick={(e) => {
                e.stopPropagation();
                remove(t.id);
              }}
            >
              &times;
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export default ToastProvider;
