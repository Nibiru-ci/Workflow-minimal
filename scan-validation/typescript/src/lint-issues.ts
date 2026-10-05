// ECHANTILLON LINT UNIQUEMENT (aucune faille de sécurité) : sert à vérifier que Codacy remonte les problèmes de style/qualité.
var unusedVariable = 1
var duplicated = 1
var duplicated = 2

function  badlyFormatted( a,b ){
  if (a == b) { console.log("equal") }
  let neverUsed = 3
  return
}

export {}
