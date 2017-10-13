<?php	
// error_reporting(E_ALL);


function findBanks($bid,$bg,$quantity,$type='RBC'){
	$ARRSIZE =10;
	$bankTable="bank_details";
	$inventoryTable="inventory";

	include 'db.php';

	$qry="SELECT * from `$bankTable` where `bid`='$bid'";
	$qry=$cxn->query($qry);
	$reqBank=$qry->fetch_assoc();
	$tmp=explode(",", $reqBank['location']);
	$reqBank['lat']=$tmp[0];
	$reqBank['lng']=$tmp[1];

	$qry="SELECT b.bid,name,location,city,`$type` from `$bankTable` as b,`$inventoryTable` as i where `city` like '".$reqBank['city']."' and b.bid=i.bid and `$type` not like ''  and b.bid<>".$bid;	
	// echo $qry;
	$qry=$cxn->query($qry);
	$banks = array();
	$dists=array();

	while($row=$qry->fetch_assoc()){
		$row[$type]=json_decode($row[$type])->$bg;
		
		if($row[$type]>0){
			$tmp=explode(",", $row['location']);
			$row['lat']=$tmp[0];
			$row['lng']=$tmp[1];
			$row['status']=0;
			$row['dist']=getDistanceFromLatLonInKm($row['lat'],$row['lng'],$reqBank['lat'],$reqBank['lng']);
			$dists[]=$row['dist'];
			$banks[]=$row;
		}		
	}

	array_multisort($dists, SORT_ASC, $banks);

	for ($i=0; $i < min($ARRSIZE,count($banks)); $i++) { 
		if($banks[$i][$type]>=$quantity){
			$max=$banks[$i];
			unset($banks[$i],$i);
			break;
		}
	}
	if(isset($max)){
		array_unshift($banks,$max);
	}
	$banks=array_slice($banks, 0,min($ARRSIZE,count($banks)));
	// $arr['banks'] = array();
	$arr['msg']="success";
	$arr['banks']=$banks;
	$arr['type']="1";
	// for ($i=0; $i < count($banks); $i++) { 
	// 	$arr['banks'][]=$banks[$i]['bid'];
	// }

	return json_encode($arr);
}

// findDonors(17,'AN');
function findDonors($bid,$bg){
	$ARRSIZE =10;
	$bankTable="bank_details";	
	$userTable="user_details";

	include 'db.php';

	$qry="SELECT * from `$bankTable` where `bid`='$bid'";
	// echo $qry;
	$qry=$cxn->query($qry);
	$reqBank=$qry->fetch_assoc();
	$tmp=explode(",", $reqBank['location']);
	$reqBank['lat']=$tmp[0];
	$reqBank['lng']=$tmp[1];

	$qry="SELECT * from `$userTable` as u where `city` like '".$reqBank['city']."' and blood_group like '$bg'";
	 // and DATEDIFF(now(),(SELECT timestamp from `donations` as a where a.uid like u.uid ORDER BY timestamp LIMIT 1))>=90";	
	// echo $qry;
	$qry=$cxn->query($qry);
	$users = array();
	$dists=array();

	while($row=$qry->fetch_assoc()){		
		$qry1="SELECT timestamp from `donations` where uid like '".$row['uid']."' order by timestamp desc LIMIT 1";
		$qry1=$cxn->query($qry1);
		if($qry1->num_rows ==1){
			$qry1=$qry1->fetch_assoc();
			$now = time();
			$last = strtotime($qry1['timestamp']);
			$diff = floor(($now-$last)/(60*60*24));
			if($diff>=90){			
				$row['dist']=getDistanceFromLatLonInKm($row['lat'],$row['lng'],$reqBank['lat'],$reqBank['lng']);
				$dists[]=$row['dist'];
				$row['status']=0;
				$users[]=$row;
			}		
		}else{
			$row['dist']=getDistanceFromLatLonInKm($row['lat'],$row['lng'],$reqBank['lat'],$reqBank['lng']);
			$dists[]=$row['dist'];
			$row['status']=0;
			$users[]=$row;
		}
	}

	array_multisort($dists, SORT_ASC, $users);

	$users=array_slice($users, 0,min($ARRSIZE,count($users)));
	// $arr['users'] = array();
	$arr['msg']="success";
	$arr['users']=$users;
	$arr['type']="2";
	// for ($i=0; $i < count($users); $i++) { 
	// 	$arr['users'][]=$users[$i]['uid'];
	// }
	// echo json_encode($arr);
	return json_encode($arr);
}

// echo findBanksForSMS('mumbai','BP',3);
function findBanksForSMS($city,$bg,$quantity,$type='RBC'){
	$ARRSIZE =10;
	$bankTable="bank_details";	
	$inventoryTable="inventory";

	include 'db.php';
	
	$qry="SELECT b.bid,location,city,`$type` from `$bankTable` as b,`$inventoryTable` as i where `city` like '".$city."' and b.bid=i.bid and `$type` not like ''";	
	// echo $qry;
	$qry=$cxn->query($qry);
	$banks = array();

	while($row=$qry->fetch_assoc()){
		$row[$type]=json_decode($row[$type])->$bg;
		
		if($row[$type]>0){
			$tmp=explode(",", $row['location']);
			$row['lat']=$tmp[0];
			$row['lng']=$tmp[1];
			$banks[]=$row;
		}
	}	

	for ($i=0; $i < min($ARRSIZE,count($banks)); $i++) { 
		if($banks[$i][$type]>=$quantity){
			$max=$banks[$i];
			unset($banks[$i],$i);
			break;
		}
	}
	if(!isset($max)){
		$max=$banks[0];
		$arr['req']=findBanks($max['bid'],$bg,$quantity);
	}
	$arr['bank']=$max;
	$arr['msg']="success";
	$arr['type']="3";
	$tmp = RandomString();
	$arr['url']=$tmp;

	$gfile = fopen("upload/".$tmp.".json", "w");	
	// fwrite($gfile, $arr);
	fwrite($gfile, json_encode($arr));
	// fwrite($gfile, string(json_encode($arr)));

	fclose($gfile);

	return json_encode($arr);	
}

function RandomString()
{
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $randstring = '';
    for ($i = 0; $i < 10; $i++) {
        $randstring .= $characters[rand(0, strlen($characters))];
    }
    return $randstring;
}

function getDistanceFromLatLonInKm($lat1, $lon1, $lat2, $lon2) {
    $R = 6371; // Radius of the earth in km
    $dLat = deg2rad($lat2 - $lat1);  // deg2rad below
    $dLon = deg2rad($lon2 - $lon1);
    $a
            = sin($dLat / 2) * sin($dLat / 2)
            + cos(deg2rad($lat1)) * cos(deg2rad($lat2))
            * sin($dLon / 2) * sin($dLon / 2);
    $c = 2 * atan2(sqrt($a), sqrt(1 - $a));
    $d = $R * $c; // Distance in km
    return $d;
}
	
	if(isset($_POST['type'])){

		include 'db.php';
		foreach ($_POST as $key => $value) {
			$_POST[$key]=$cxn->real_escape_string($value);
		}
		extract($_POST);

		// print_r($_POST);
		if($type=="1"){
			if(isset($bid) && isset($bg) && isset($quantity)){
				echo findBanks($bid,$bg,$quantity);
			}else{
				$arr['msg']='error';
				echo json_encode($arr);	
			}
		}else if($type=="2"){
			if(isset($bid) && isset($bg)){
				echo findDonors($bid,$bg);
			}else{
				$arr['msg']='error';
				echo json_encode($arr);	
			}
		}else if($type=="3"){
			if(isset($city) && isset($bg) && isset($quantity)){
				echo findBanksForSMS($city,$bg,$quantity);
			}else{
				$arr['msg']='error';
				echo json_encode($arr);	
			}
		}else{
			$arr['msg']='error';
			echo json_encode($arr);	
		}
	}else{
		$arr['msg']='error';
		echo json_encode($arr);
	}
?>