// ECHANTILLON VOLONTAIREMENT VULNERABLE : sert uniquement à vérifier que les scans CI détectent bien les problèmes.
import { exec } from "child_process";
import * as crypto from "crypto";
import * as http from "http";
import _ from "lodash";

// SAST / secret : identifiants en dur (valeurs factices)
const DB_PASSWORD = "SuperSecretPassw0rd!";
const API_KEY = "k8Jd93hFs02LmZp7QxVn4TrWy61BcAe5";

http
  .createServer((req, res) => {
    const url = new URL(req.url ?? "/", "http://localhost");
    const name = url.searchParams.get("name") ?? "";

    // SAST : injection de commande
    exec(`echo ${name}`, (_err, stdout) => res.write(stdout));

    // SAST : eval sur une entrée utilisateur
    eval(url.searchParams.get("expr") ?? "1");

    // SAST : injection SQL (concaténation)
    const query = "SELECT * FROM users WHERE name = '" + name + "'";

    // SAST : hachage faible
    const hash = crypto.createHash("md5").update(DB_PASSWORD).digest("hex");

    // SAST : XSS réfléchi
    res.end(`<h1>Hello ${name}</h1>${query}${hash}${API_KEY}${_.VERSION}`);
  })
  .listen(8080);
