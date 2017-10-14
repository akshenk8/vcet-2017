<?php	
// error_reporting(E_ALL);
	if(!(isset($_POST['uid']) &&
		isset($_POST['firebase']))) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);		
		$table="user_details";

		$bid=$cxn->real_escape_string($bid);
		$firebase=$cxn->real_escape_string($firebase);

		$qry="UPDATE `$table` set `firebase`='$firebase' where `uid` like '$uid'";
		$qry=$cxn->query($qry);				
		if($qry){
			$arr['msg']='success';				
		}else{
			$arr['msg']='error';
		}
	}
?>
