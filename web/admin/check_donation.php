<?php	
 //error_reporting(E_ALL);
	if(!(isset($_POST['uid']) 
		 && isset($_POST['bid']))) {
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
		$table3="user_details";

		$qry="SELECT `bid`,`active`,`name` from `$table2` where `bid`='$bid'";
		$qry=$cxn->query($qry);
		$q=$qry->num_rows;
		if($q==1){			
			$qry=$qry->fetch_assoc();
			if($qry['active']==1){	
				$name =$qry['name'];	
				
				$qry = "SELECT `timestamp` as t from `$table1` where `uid` like '$uid' order by `timestamp` desc LIMIT 1";
				$qry=$cxn->query($qry);
				$q=$qry->num_rows;

				if($q==1){
					$qry=$qry->fetch_assoc();
					$t=$qry['t'];

					$qry="SELECT `uid`,`name`,`blood_group`,DATEDIFF(now(),'$t') as el from `$table3` where `uid` like '$uid'";
					$qry=$cxn->query($qry);
					$qry=$qry->fetch_assoc();
					$arr['data']=$qry;
					$arr['bname']=$name;
					// if($qry){
						$arr['msg']='success';				
					// }else{
					// 	$arr['msg']='error';
					// }
				}else{
					$qry="SELECT `uid`,`name`,`blood_group` from `$table3` where `uid` like '$uid'";
					$qry=$cxn->query($qry);
					$qry=$qry->fetch_assoc();
					$qry['el']='90';
					$arr['data']=$qry;
					$arr['bname']=$name;
					// if($qry){
						$arr['msg']='success';				
					// }else{
					// 	$arr['msg']='error';
					// }
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