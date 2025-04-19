#!/bin/bash
# Script to uninstall ALL Helm releases and Persistent Volume Claims (PVCs)
# in the default namespace on macOS.
# USE WITH EXTREME CAUTION!  This will remove everything managed by Helm
# and all PVCs in the default namespace, potentially causing data loss.

# Function to display messages
msg() {
  echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# 1. Check if Helm is installed
if ! command -v "helm" &>/dev/null; then
  msg "Error: Helm is not installed. Please install Helm and try again."
  exit 1
fi

# 2. Check if kubectl is installed
if ! command -v "kubectl" &>/dev/null; then
  msg "Error: kubectl is not installed. Please install kubectl and try again."
  exit 1
fi

# 3. Set the namespace to default
namespace="default"
msg "Setting namespace to 'default'."

# 4. Get all Helm releases in the default namespace
msg "Getting all Helm releases in the '$namespace' namespace..."
release_names=$(helm list -n "$namespace" | awk 'NR>1 {print $1}') # Get release names, skip header

if [ -z "$release_names" ]; then
  msg "No Helm releases found in the '$namespace' namespace."
else
  # 5. Uninstall each Helm release
  msg "Found the following Helm releases in the '$namespace' namespace:"
  echo "$release_names" | while read -r release_name; do
    msg "Uninstalling Helm release: $release_name"
    helm uninstall "$release_name" -n "$namespace"
    if [ $? -eq 0 ]; then
      msg "Helm release '$release_name' uninstalled successfully."
    else
      msg "Error: Failed to uninstall Helm release '$release_name'."
      # Continue uninstalling other releases, even if one fails
    fi
  done
fi

# 6. Delete all Persistent Volume Claims (PVCs) in the default namespace
msg "Getting all Persistent Volume Claims (PVCs) in the '$namespace' namespace..."
pvc_names=$(kubectl get pvc -n "$namespace" -o name) # Get PVC names

if [ -z "$pvc_names" ]; then
  msg "No Persistent Volume Claims (PVCs) found in the '$namespace' namespace."
else
  # Prompt the user for confirmation before deleting PVCs
  read -r -p "$(msg "WARNING: The following Persistent Volume Claims (PVCs) will be deleted in the '$namespace' namespace:
$pvc_names
This action will PERMANENTLY DELETE the associated data.  Are you sure you want to continue? (y/N) ")" response
  if [[ "$response" =~ ^[yY](es)?$ ]]; then
    msg "Deleting Persistent Volume Claims (PVCs)..."
    echo "$pvc_names" | while read -r pvc_name; do
      kubectl delete -n "$namespace" "$pvc_name" -f
      if [ $? -eq 0 ]; then
        msg "Persistent Volume Claim '$pvc_name' deleted successfully."
      else
        msg "Error: Failed to delete Persistent Volume Claim '$pvc_name'."
        # Continue deleting other PVCs, even if one fails
      fi
    done
  else
    msg "Aborting PVC deletion."
  fi
fi

msg "Uninstallation process completed."
