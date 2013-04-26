<?php
    $data = file_get_contents('php://input');
$json = json_decode($data);
$service = $json->{'service'};


$mensaje = $json ->{'DeviceName'} . " <br> " . $json ->{'Latitude'} . ":" . $json ->{'Longitude'} ;




/*$im = imagecreatefromstring($json->{'image'});
if ($im !== false) {
    header('Content-Type: image/png');
    imagepng($im);
    imagedestroy($im);
}
else {
    echo 'Ocurrió un error.';
}*/


mail('jorgebernabeumira@gmail.com', 'Information Received', $mensaje);
?>