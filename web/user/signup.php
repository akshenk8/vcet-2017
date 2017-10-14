<?php
    $resJson = ['hasError'=>0];

    if(!isset($_POST['uid']) || !isset($_POST['name']) || !isset($_POST['mobile_no']) || !isset($_POST['blood_group']) || !isset($_POST['gender']) || !isset($_POST['dob'])){
        echo 'Field is not set!';
        exit; 
    } 
    if(empty($_POST['uid']) || empty($_POST['name'] || empty($_POST['mobile_no'])) || empty($_POST['blood_group']) || empty($_POST["gender"]) || empty($_POST['dob'])){
        echo 'Fields value cannot be empty!';
        exit; 
    } 
    if(strlen(trim($_POST['uid'])) <= 0){
        echo 'Mobile No cannot be white space!';
        exit; 
    } 


    include("db.php");

    $uid = mysqli_real_escape_string($cxn,trim(filter_var($_POST['uid'])));
    $name = mysqli_real_escape_string($cxn,trim(filter_var($_POST['name'])));
    $mobile_no = $_POST['mobile_no'];
    $blood_group = mysqli_real_escape_string($cxn,trim(filter_var($_POST['blood_group'])));
    $gender = mysqli_real_escape_string($cxn,trim(filter_var($_POST['gender'])));
    $dob = mysqli_real_escape_string($cxn,trim(filter_var($_POST['dob'])));
    

    $query = "SELECT `uid` FROM `user_details` WHERE `uid`='$uid'";

    //echo $query;
    $run = mysqli_query($cxn,$query);
    if(mysqli_num_rows($run) == 1){
        mysqli_close($cxn);
        echo 'User is already exist!';
        exit; 
    }


    $query = "INSERT INTO `user_details` (`uid`,`name`,`mobile_no`,`blood_group`,`gender`,`dob`) VALUES ('$uid','$name','$mobile_no','$blood_group','$gender','$dob')";
    // echo $query;
    if(!mysqli_query($cxn,$query)){
        mysqli_close($cxn);
        echo 'User not signuped due to some reason!';
        exit; 
    }
    mysqli_close($cxn);
    echo "true";
    exit;
?>