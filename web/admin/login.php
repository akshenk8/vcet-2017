<?php
	if(!(isset($_POST['name']) &&
		isset($_POST['pass']))) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table="bank_details";
		
		$name=$cxn->real_escape_string($name);
		$pass=$cxn->real_escape_string($pass);
		$qry="SELECT `bid`,`active` from `$table` where `username` like '$name' and `password` like '$pass'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){
				$arr['msg']='success';
				$arr['bid']=$qry['bid'];

				$qry="UPDATE `$table` set `last_login`=now() where `bid`={$arr['bid']}";
				$qry=$cxn->query($qry);
			}
			else{
				$arr['msg']='confirmation';
			}
			echo json_encode($arr);
		}
		else{
			$arr['msg']='error';
			echo json_encode($arr);
		}
	}
?>
