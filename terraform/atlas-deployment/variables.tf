variable "kubernetes-namespace" {
  description = "The namespace for the kubernetes deployment"
  default     = "dagx"
}

variable "chart-dir" {
  description = "The directory where the local atlas helm chart is located"
  default     = "atlas-chart"
}

variable "cluster_name" {
  type = string
}

variable "kubeconfig" {
  type = string
}

variable "resourcesuffix"{
  type= string
}

variable "location"{
    type= string
    default="westeurope"
}

variable "tenant_id" {
    type= string
}
