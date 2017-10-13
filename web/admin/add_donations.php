<?php	
 //error_reporting(E_ALL);
	if(!(isset($_POST['uid']) 
		 && isset($_POST['bid']) 
		 && isset($_POST['quantity']) )) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}else{
		include 'db.php';

		foreach ($_POST as $key => $value) {
			$_POST[$key]=$cxn->real_escape_string($value);
		}
		extract($_POST);
		$table1="donations";	
		$table2="bank_details";	

		$qry="SELECT `bid`,`active` from `$table2` where `bid`='$bid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){			
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){			
				
				$qry="INSERT INTO `$table1` values('$uid','$bid',now(),'$quantity')";
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