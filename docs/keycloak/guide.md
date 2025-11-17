KeyCloak

1. Setup Client
https://www.youtube.com/watch?v=g7mhnZfbbOk&list=PLHXvj3cRjbzs8TaT-RX1qJYYK2MjRro-P&index=3


# Configuring Keycloak Email/SMTP Settings

Keycloak email is configured in the **Admin Console**, not via Helm values.

---
Using Keycloak Admin Console (Recommended for Initial Setup)

### 1. Access the Keycloak Admin Console
- **URL:** `http://keycloak.default.svc.cluster.local:80` *(or your Keycloak URL)*
- **Login:** Use admin credentials from your `values.yaml` (e.g., `user` / `password`)

---

### 2. Navigate to Email Settings
- Select your **realm** (e.g., `master` or your custom realm)
- Go to **Realm Settings → Email** tab

---

### 3. Configure SMTP Settings

| Setting | Example Value | Description |
|----------|----------------|-------------|
| **Host** | `smtp.gmail.com` | Your SMTP server |
| **Port** | `587` (for TLS) or `465` (for SSL) | SMTP port |
| **From** | `noreply@yourdomain.com` | Sender email address |
| **From Display Name** | `Your Application Name` | Name displayed in email |
| **Reply To** | `support@yourdomain.com` | Email for replies |
| **Reply To Display Name** | `Support Team` | Display name for reply address |

---

### 4. Enable Authentication
- ✅ Enable **Authentication**
- Enter your **SMTP username** and **password**

---

### 5. Enable StartTLS (if using port 587)
- ✅ Enable **StartTLS**

---

### 6. Test the Connection
- Click **Test connection** to verify the configuration

# Important Notes for Keycloak Email Configuration

### 1. Enable "Forgot Password"
- Navigate to **Realm Settings → Login** tab
- Ensure **Forgot Password** is enabled

---

### 2. Email Templates
- Customize email templates at:  
  **Realm Settings → Email tab → Email Templates**

---

### 3. Security
- For Gmail SMTP, **use an App Password**, not your regular Gmail password

---

### 4. Testing
- For development or testing, use **Mailtrap** or a similar service

---

### 5. Environment Variables
- While you **can add environment variables** in your Helm chart (`configmap-env-vars.yaml`), Keycloak **does not support configuring email via environment variables directly**
- Email settings **must be set via Admin Console or Keycloak Admin API**


### Qt Client Requirements

When configuring the **Qt Client**, make sure to provide the following:

1. **Web Origin**  
   This defines the trusted base URL from which authentication requests will originate.  
   Example: `http://localhost:8080`

2. **Redirect URI**  
   After authentication, the authorization server will redirect the user back to this URI.  
   Example: `http://localhost:8080/callback`

Both values must be set correctly for the Qt Client to authenticate successfully.