#!/usr/bin/env bash
# Crea 2 GB de memoria swap (necesario en la t3.micro de 1 GB para correr 2 backends + PostgreSQL).
# Uso: sudo bash scripts/swap.sh
set -euo pipefail
if swapon --show | grep -q /swapfile; then echo "Ya hay swap:"; swapon --show; exit 0; fi
fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
grep -q '/swapfile' /etc/fstab || echo '/swapfile none swap sw 0 0' >> /etc/fstab
sysctl -w vm.swappiness=10
grep -q 'vm.swappiness' /etc/sysctl.conf || echo 'vm.swappiness=10' >> /etc/sysctl.conf
free -h
