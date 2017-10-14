<?php	
// error_reporting(E_ALL);
	// print_r($_POST);
	if(!(isset($_POST['uid']))) {
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

		$qry="SELECT `city` from `user_details` where `uid` like '$uid'";
		// echo $qry;
		$qry=$cxn->query($qry);
		$qry=$qry->fetch_assoc();
		$city=$qry['city'];

		$qry="SELECT * from `$table1` where status<>0 and `address` like '%$city%'";

		// echo "<br>".$qry;
		$qry=$cxn->query($qry);
		
		$data = array();
		while($row=$qry->fetch_assoc()){
			$data[]=$row;
		}
		$arr = array();
		$arr['msg']='success';				
		$arr['camps']=$data;
		echo json_encode($arr);	
	}
?>