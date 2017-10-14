<?php
// include 'firebase.php';
// $f= new Firebase();
// $f->send($to[0],"'msg':'hi'");

$last = strtotime("2017-10-13 17:50:32");
$next = strtotime("+90 days",strtotime("2017-10-13 17:50:32"));

echo $last."<br>".$next;

$diff =floor(($next-$last)/(60*60*24));
echo "<br>".$diff;

// if(isset($_POST['to']) && isset($_POST['message'])){
//         // include 'db.php';

//         foreach ($_POST as $key => $value) {
//             $_POST[$key]=$cxn->real_escape_string($value);
//         }
//         extract($_POST);
//         $to=json_decode($to);
//         print_r( $to );
//         // $fb= Firebase();
//         // for ($i=0; $i <coun($to) ; $i++) { 
//         //     $fb->send($to[$i],$message);
//         // }

//         $table1="donations";    
//         $table2="bank_details"; 

//         $arr['msg']='success';
//         echo json_encode($arr);
//     }else{
//         $arr['msg']='error';
//         echo json_encode($arr);
//     }
?>