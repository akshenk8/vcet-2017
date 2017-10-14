<?php
if(isset($_POST['uid'])){

	include 'db.php';

	foreach ($_POST as $key => $value) {
		$_POST[$key]=$cxn->real_escape_string($value);
	}
	extract($_POST);

	$qry1="SELECT timestamp from `donations` where uid like '".$uid."' order by timestamp desc LIMIT 1";
	$qry1=$cxn->query($qry1);
	if($qry1->num_rows ==1){
		$qry1=$qry1->fetch_assoc();
		$now = time();
		$last = strtotime($qry1['timestamp']);
		$next = strtotime("+90 days",strtotime($qry1['timestamp']));
		$diff =90-floor(($next-$last)/(60*60*24));
		// $diff = floor(($now-$last)/(60*60*24));
		$arr['diff']=$diff;	
	}else{
		$arr['diff']=90;
	}
	$arr['msg']='success';
	echo json_encode($arr);
}else{
	$arr['msg']='error';
	echo json_encode($arr);
}

?>