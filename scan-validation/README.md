# Validation des scans CI

Ce dossier contient du code **volontairement vulnérable** (et de faux secrets) pour vérifier que `ci.yml` détecte bien chaque catégorie de problème.
**Ne jamais le fusionner dans la branche principale** : utiliser une branche jetable.

## Procédure

```bash
git checkout -b test/scan-validation
git add . && git commit -m "test: échantillon de validation des scans"
git push -u origin test/scan-validation   # puis ouvrir une PR (draft) vers main
```

Aucune modification de `ci.yml` n'est nécessaire : `sca.yml` découvre automatiquement tous les `Dockerfile` du dépôt (ici `docker/Dockerfile` et `docker/Dockerfile.lint`) et lance un scan par image.

Une fois la vérification faite : fermer la PR et **supprimer la branche** (les faux secrets resteraient dans l'historique git sinon).

## Résultats attendus (tous les jobs échouent, sauf l'image témoin `Dockerfile.lint`)

| Job | Détection attendue |
|---|---|
| SCA - Dépendances | `lodash 4.17.20`, `minimist 1.2.5` (typescript) ; `guzzlehttp/guzzle 6.5.4` (php) ; `golang.org/x/crypto`, `gopkg.in/yaml.v2` (go) ; `log4j-core 2.14.1`, `jackson-databind 2.9.8` (java) |
| SCA - Image (`Dockerfile`) | CVE de l'OS `node:14.0.0-buster-slim` + `lodash 4.17.20` dans `/app/node_modules` : **doit échouer** |
| SCA - Image (`Dockerfile.lint`) | `alpine:latest` à jour : **doit passer** (témoin : prouve que le scan tourne et ne signale pas tout) |
| SAST - Codacy | injections SQL/commande, `eval`, MD5, XSS, XXE, désérialisation, TLS désactivé, mots de passe en dur ; plus les problèmes de lint ci-dessous |
| Secret scanning | `AKIA…` et `ghp_…` dans `secrets/leaked.env`, clé API générique dans chaque fichier source |

Les valeurs des secrets doivent apparaître **masquées** (`REDACTED`) dans le résumé et l'artifact.

## Vérifier que Codacy prend en compte le lint

Les fichiers `lint-issues.ts`, `lint-issues.php`, `lint.go`, `LintIssues.java` et `Dockerfile.lint` ne contiennent **aucune faille de sécurité**, uniquement des problèmes de style/qualité. Si leurs fichiers apparaissent dans le tableau du résumé SAST (et dans l'artifact `sast-codacy-report`), le lint est bien pris en compte. Le résumé indique aussi la répartition par niveau (`error`, `warning`, `note`) : avec `gh-code-scanning-compat`, les problèmes non liés à la sécurité sont déclassés d'un niveau.

| Fichier | Outil Codacy attendu | Exemples de règles déclenchées |
|---|---|---|
| `typescript/src/lint-issues.ts` | ESLint | `no-var`, `no-redeclare`, `eqeqeq`, `no-unused-vars`, `no-console`, `semi` |
| `php/lint-issues.php` | PHP_CodeSniffer (PSR) | nom de classe/méthode, visibilité manquante, accolades, balise fermante |
| `go/lint.go` | Revive/golint | noms avec `_`, type redondant, formatage `gofmt` |
| `java/.../LintIssues.java` | Checkstyle / PMD | nom de classe, imports `*` inutilisés, bloc `catch` vide, accolades manquantes, nombre magique |
| `docker/Dockerfile.lint` | Hadolint | `DL3007` (`latest`), `DL4000` (`MAINTAINER`), `DL3000`, `DL3020` (`ADD`), `DL3018`, `DL3003` |

Les règles réellement actives dépendent de la configuration du projet Codacy (avec `CODACY_PROJECT_TOKEN`) ou des patterns par défaut (sans token). Si aucun fichier de lint n'apparaît alors que le token est fourni, vérifier dans Codacy (*Code patterns*) que les patterns de style de l'outil concerné sont activés.

## Limites

- `php/composer.lock` est écrit à la main (composer n'était pas disponible) ; `go/go.sum` est absent. Suffisant pour les scans, pas pour compiler.
- Le nombre exact d'issues Codacy dépend de la configuration distante du projet (patterns activés).
- Si GitHub bloque le push (push protection sur les faux tokens), valider le déblocage depuis le lien fourni : ce sont des valeurs inventées.
