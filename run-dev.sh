#!/usr/bin/env bash
# Lokal dev: .env dan secret larni yuklab, ilovani ishga tushiradi.
set -euo pipefail
cd "$(dirname "$0")"

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

exec ./mvnw spring-boot:run
