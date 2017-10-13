<?php
    $resJson = ['hasError'=>0];
    if(!isset($_POST['uid'])){
        echo 'Field is not set!';
        exit; 
    } 
    if(empty($_POST['uid'])){
        echo 'Fields value cannot be empty!';
        exit; 
    } 
    if(strlen(trim($_POST['uid'])) <= 0){
        echo 'Mobile No cannot be white space!';
        exit; 
    } 


    include("db.php");

    $uid = mysqli_real_escape_string($cxn,trim(filter_var($_POST['uid'])));
    

    $query = "SELECT `uid` FROM `user_details` WHERE `uid`='$uid'";

    //echo $query;
    $run = mysqli_query($cxn,$query);
    if(mysqli_num_rows($run) >= 1){


        $sql = "SELECT `name`,`timestamp`,`quantity` FROM `donations` AS `d`,`bank_details` AS `b` WHERE `b`.`bid` = `d`.`bid` AND `uid` = '$uid' ORDER BY `timestamp` DESC ";


        $historyData = array();

        $data = mysqli_query($cxn,$sql);

        while($row = mysqli_fetch_assoc($data)){
            $historyData[] = $row;
        }
        

        print_r(json_encode($historyData));
        die(0);

        mysqli_close($cxn);        
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