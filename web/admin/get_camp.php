<?php	
// error_reporting(E_ALL);
	if(!(isset($_POST['bid']))) {
		$arr['msg']='error';
		echo json_encode($arr);		
	}
	else{
		include 'db.php';

		foreach ($_POST as $key => $value) {
			$_POST[$key]=$cxn->real_escape_string($value);
		}
		extract($_POST);
		$table1="camp";
		$table2="bank_details";

		$qry="SELECT `bid`,`active` from `$table2` where `bid`='$bid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){
				$qry="SELECT * from `$table1` where `bid`='$bid'";								
				$qry=$cxn->query($qry);
				$events= array();
				while($row = $qry->fetch_assoc()){
					$events[]=$row;
				}
				$arr['camps']=$events;
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