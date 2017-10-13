<?php	
error_reporting(E_ALL);
	if(!(isset($_POST['bid']))) {
		$arr['msg']='error';
		echo json_encod	e($arr);		
	}
	else{
		include 'db.php';
		extract($_POST);
		$table1="inventory";
		$table2="bank_details";
		
		$bid=$cxn->real_escape_string($bid);

		$qry="SELECT `bid`,`active` from `$table2` where `	`='$bid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){				
				$qry="SELECT * from `$table1` where `bid`='$bid'";
				$qry=$cxn->query($qry);
				$q=$qry->num_rows;
				$types= array('WBC','RBC','FFP','PC','CRY');

				if($q != 1){
					$q="INSERT INTO `$table1` values('$bid','','','','','')";
					$cxn->query($q);
					foreach ($types as $key => $value) {
						$arr[$value]='';	
					}
				}else{
					$qry=$qry->fetch_array();
					foreach ($types as $key => $value) {
						$arr[$value]=$qry[$value];	
					}
				}				

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
