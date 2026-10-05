// ECHANTILLON LINT UNIQUEMENT (aucune faille de sécurité) : sert à vérifier que Codacy remonte les problèmes de style/qualité.
package main

import "fmt"

type my_struct struct{ Field_Name int }

func Bad_Name( a int,b int ) int {
	var result int = 0
	if a == b { result = 1 } else { result = 2 }
	fmt.Println( "result" , result )
	return result
}
