<?php
// ECHANTILLON LINT UNIQUEMENT (aucune faille de sécurité) : sert à vérifier que Codacy remonte les problèmes de style/qualité.
class bad_class_name
{
    var $PublicField = 1;

    function Do_Something($a,$b) {
        $unused = 1;
        if($a==$b){ return true; } else { return false; }
    }
}
?>
