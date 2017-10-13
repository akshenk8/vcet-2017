<?php
    $resJson = ['hasError'=>0];
    if(!isset($_POST['id'])){
        echo 'Field is not set!';
        exit; 
    } 
    if(empty($_POST['id'])){
        echo 'Fields value cannot be empty!';
        exit; 
    } 
    if(strlen(trim($_POST['id'])) <= 0){
        echo 'Mobile No cannot be white space!';
        exit; 
    } 


    include("db.php");

    $id = mysqli_real_escape_string($cxn,trim(filter_var($_POST['id'])));
    

    $query = "SELECT `uid` FROM `user_details` WHERE `uid`='$id'";

    //echo $query;
    $run = mysqli_query($cxn,$query);
    if(mysqli_num_rows($run) == 1){
        mysqli_close($cxn);
        echo 'User is already exist!';
        exit; 
    }
    // $md5pass = md5($email.$password);

    // $query = "INSERT INTO `user`(`email`,`password`) VALUES ('$email','$md5pass')";
    // if(!mysqli_query($con,$query)){
    //     mysqli_close($con);
    //     echo 'User not signuped due to some reason!';
    //     exit; 
    // }
    mysqli_close($cxn);
    echo "true";
    exit;
?>