// ECHANTILLON VOLONTAIREMENT VULNERABLE : sert uniquement à vérifier que les scans CI détectent bien les problèmes.
package main

import (
	"crypto/md5"
	"crypto/tls"
	"database/sql"
	"fmt"
	"net/http"
	"os/exec"

	"golang.org/x/crypto/ssh"
	"gopkg.in/yaml.v2"
)

// SAST / secret : identifiants en dur (valeurs factices)
const dbPassword = "SuperSecretPassw0rd!"
const apiKey = "k8Jd93hFs02LmZp7QxVn4TrWy61BcAe5"

func handler(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")

	// SAST : injection de commande
	out, _ := exec.Command("sh", "-c", "echo "+name).Output()

	// SAST : injection SQL
	db, _ := sql.Open("mysql", "root:"+dbPassword+"@/app")
	db.Query("SELECT * FROM users WHERE name = '" + name + "'")

	// SAST : hachage faible
	sum := md5.Sum([]byte(dbPassword))

	// SAST : vérification TLS désactivée
	_ = &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}}

	// SAST : clé d'hôte SSH non vérifiée
	_ = ssh.InsecureIgnoreHostKey()

	var data map[string]interface{}
	yaml.Unmarshal([]byte(name), &data)

	fmt.Fprintf(w, "%s %x %s", out, sum, apiKey)
}

func main() {
	http.HandleFunc("/", handler)
	// SAST : serveur sans timeout
	http.ListenAndServe(":8080", nil)
}
