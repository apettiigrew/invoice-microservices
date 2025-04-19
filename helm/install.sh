#!/bin/bash
# Script to install local services using Helm in a specific order

# Function to display messages
msg() {
  echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# 1. Check if Helm is installed
if ! command -v "helm" &>/dev/null; then
  msg "Error: Helm is not installed. Please install Helm and try again."
  exit 1
fi

# 2. Define the services and their locations AND the order
declare -A services=(
  ["keycloak"]="./keycloak"
  ["rabbit"]="./rabbitmq"
  ["prometheus"]="./kube-prometheus"
  ["loki"]="./grafana-loki"
  ["tempo"]="./grafana-tempo"
  ["grafana"]="./grafana"
  # ["eazybank"]="./environments/dev-env"
)
# 2b. Define the order of installation
installation_order=(
  "keycloak"
  "rabbit"
  "prometheus"
  "loki"
  "tempo"
  "grafana"
  # "eazybank"
)

# 3. Define the namespace
namespace="default"

# 4. Use the default namespace
msg "Using namespace 'default'."

# 5. Install the services in the specified order
for service_name in "${installation_order[@]}"; do
  local service_path="${services[$service_name]}" #get path

  if [[ -d "$service_path" ]]; then
    msg "Installing $service_name..."
    msg "Installing $service_path..."
    helm install "$service_name" "$service_path" -n "$namespace"
    if [ $? -ne 0 ]; then
      msg "Error: Failed to install $service_name."
      #  Do NOT exit on a single failure, continue with the rest of the installation
    fi
  else
    msg "Warning: Directory for $service_name not found: $service_path. Skipping."
  fi
done

msg "Installation process completed."
