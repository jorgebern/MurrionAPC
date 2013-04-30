<?php

    $target_path  = "./";
    $target_path = $target_path . basename( $_FILES['file']['name']);

    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
        $message =  "The file has been uploaded";
    } 
    else {
        $message = "There was an error uploading the file, please try again!";
       
    }
     mail('jorgebernabeumira@gmail.com', 'Information Received', $message);

?>
