<?php
error_reporting(E_ALL);
	if(!(isset($_POST['uid'])) &&
		 isset($_POST['lat']) &&
		 isset($_POST['lat']) &&
		 isset($_POST['acc'])) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="user_details";
		
		$uid=$cxn->real_escape_string($uid);
		$loc=$cxn->real_escape_string($loc);
		
		$qry="SELECT `uid` from `$table` where `uid`='$uid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){

			$qry="UPDATE `$table` set `lat`='$lat' , `lng`='$lng', `acc`='$acc', `last_updated`=now() where `uid` like '$uid'";				
			$qry=$cxn->query($qry);
			if($qry){
				$arr['msg']='success';
			}else{
				$arr['msg']='error'.$cxn->error;
			}
		
			echo json_encode($arr);
		}
		else{			
			$arr['msg']='error';
			echo json_encode($arr);
		}
	}
?>