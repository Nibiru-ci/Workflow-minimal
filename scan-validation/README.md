# Validation des scans CI

Ce dossier contient du code **volontairement vulnérable** (et de faux secrets) pour vérifier que `ci.yml` détecte bien chaque catégorie de problème.
**Ne jamais le fusionner dans la branche principale** : utiliser une branche jetable.

## Procédure

```bash
git checkout -b test/scan-validation
git add . && git commit -m "test: échantillon de validation des scans"
git push -u origin test/scan-validation   # puis ouvrir une PR (draft) vers main
```

Pour que `sca-image` scanne le Dockerfile de test (il cherche `Dockerfile` à la racine par défaut), ajouter dans `ci.yml`, **sur cette branche uniquement** :

```yaml
  sca:
    uses: ./.github/workflows/sca.yml
    with:
      dockerfile: scan-validation/docker/Dockerfile
      docker-context: scan-validation/docker
```

Une fois la vérification faite : fermer la PR et **supprimer la branche** (les faux secrets resteraient dans l'historique git sinon).

## Résultats attendus (tous les jobs doivent être en échec)

| Job | Détection attendue |
|---|---|
| SCA - Dépendances | `lodash 4.17.20`, `minimist 1.2.5` (typescript) ; `guzzlehttp/guzzle 6.5.4` (php) ; `golang.org/x/crypto`, `gopkg.in/yaml.v2` (go) ; `log4j-core 2.14.1`, `jackson-databind 2.9.8` (java) |
| SCA - Image conteneur | CVE de l'OS `node:14.0.0-buster-slim` + `lodash 4.17.20` dans `/app/node_modules` |
| SAST - Codacy | injections SQL/commande, `eval`, MD5, XSS, XXE, désérialisation, TLS désactivé, mots de passe en dur, lint Dockerfile |
| Secret scanning | `AKIA…` et `ghp_…` dans `secrets/leaked.env`, clé API générique dans chaque fichier source |

Les valeurs des secrets doivent apparaître **masquées** (`REDACTED`) dans le résumé et l'artifact.

## Limites

- `php/composer.lock` est écrit à la main (composer n'était pas disponible) ; `go/go.sum` est absent. Suffisant pour les scans, pas pour compiler.
- Le nombre exact d'issues Codacy dépend de la configuration distante du projet (patterns activés).
- Si GitHub bloque le push (push protection sur les faux tokens), valider le déblocage depuis le lien fourni : ce sont des valeurs inventées.
