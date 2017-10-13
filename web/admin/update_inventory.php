<?php	
// error_reporting(E_ALL);
	if(!(isset($_POST['bid']) &&
		isset($_POST['type']) &&
		isset($_POST['data']))) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table1="inventory";
		$table2="bank_details";
		
		$bid=$cxn->real_escape_string($bid);
		$type=$cxn->real_escape_string($type);
		$data=$cxn->real_escape_string($data);

		$qry="SELECT `bid`,`active` from `$table2` where `bid`='$bid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){				
				$qry="SELECT `bid` from `$table1` where `bid`='$bid'";
				$qry=$cxn->query($qry);
				$qry=$qry->num_rows;
				if($qry != 1){
					$qry="INSERT INTO `$table1` values('$bid','','','','','')";
					$qry=$cxn->query($qry);
				}

				if($type == "WBC"){
					$qry="UPDATE `$table1` set `WBC`='$data' where `bid`='$bid'";
				}else if($type == "RBC"){
					$qry="UPDATE `$table1` set `RBC`='$data' where `bid`='$bid'";			
				}else if($type == "FFP"){
					$qry="UPDATE `$table1` set `FFP`='$data' where `bid`='$bid'";
				}else if($type == "PC"){	
					$qry="UPDATE `$table1` set `PC`='$data' where `bid`='$bid'";					
				}else if($type == "CRY"){
					$qry="UPDATE `$table1` set `CRY`='$data' where `bid`='$bid'";					
				}					
				$qry=$cxn->query($qry);				
				if($qry){
					$arr['msg']='success';				
				}else{
					$arr['msg']='error';
				}
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