<?php	
// error_reporting(E_ALL);
	if(!isset($_POST['uid'])){
		$arr['msg']='error';
		echo json_encode($arr);
	}else{
		include 'db.php';

		foreach ($_POST as $key => $value) {
			$_POST[$key]=$cxn->real_escape_string($value);
		}
		extract($_POST);
		$table1="user";		

		$qry="SELECT `rewards`,`active` from `$table1` where `uid` like '$uid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){
				$arr['msg']='success';				
				$arr['rewards']=$qry['rewards'];
			}
			else{
				$arr['msg']='confirmation';
			}
			echo json_encode($arr);
		}else{
			$arr['msg']='error';
			echo json_encode($arr);
		}
	}
?>