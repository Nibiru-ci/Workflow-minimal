<?php
// ECHANTILLON VOLONTAIREMENT VULNERABLE : sert uniquement à vérifier que les scans CI détectent bien les problèmes.

// SAST / secret : identifiants en dur (valeurs factices)
$dbPassword = "SuperSecretPassw0rd!";
$apiKey = "k8Jd93hFs02LmZp7QxVn4TrWy61BcAe5";

$name = $_GET['name'];

// SAST : injection SQL
$conn = mysqli_connect("localhost", "root", $dbPassword, "app");
$result = mysqli_query($conn, "SELECT * FROM users WHERE name = '" . $name . "'");

// SAST : injection de commande
system("ping -c 1 " . $_GET['host']);

// SAST : eval sur une entrée utilisateur
eval($_GET['code']);

// SAST : hachage faible
$hash = md5($dbPassword);

// SAST : XSS réfléchi
echo "<h1>Hello " . $name . "</h1>";

// SAST : inclusion de fichier arbitraire
include($_GET['page']);
