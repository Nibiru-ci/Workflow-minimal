# ECHANTILLON VOLONTAIREMENT VULNERABLE : sert uniquement à vérifier que les scans CI détectent bien les problèmes.
# Image de base ancienne (paquets système vulnérables) + dépendance npm vulnérable embarquée.
FROM node:14.0.0-buster-slim

WORKDIR /app
RUN npm install lodash@4.17.20 --prefix /app

# SAST (lint Dockerfile) : exécution en root, pas de HEALTHCHECK
CMD ["node", "-e", "require('lodash'); setInterval(() => {}, 1000)"]
