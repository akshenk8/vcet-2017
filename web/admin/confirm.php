

<?php
	define( 'API_ACCESS_KEY', 'AAAA2hVs1-w:APA91bFqS9FeCdjKOlMSJp5g8RDkS7R6kxbbXBonlAfEbZeYo67mD-ZPWJ8EaZLpp0Xwa95qmkl73nKctCsOmxV9sQ_pEvcogGc_7U_yyl1_rW1psCBI5aFruV3NSSl_nQjcg6SIp_fJ' );
	if(isset($_POST['fire']) && isset($_POST['data'])){

		$msg1=array("msg"=>$_POST['data']);	
		$fields = array
				(
					'to'=> $_POST['fire'],
					'data'	=> $msg1
				);	
		
		$headers = array
			(
				'Authorization: key=' . API_ACCESS_KEY,
				'Content-Type: application/json'
			);
		#Send Reponse To FireBase Server	
		$ch = curl_init();
		curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
		curl_setopt( $ch,CURLOPT_POST, true );
		curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
		curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
		curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
		curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
		$result = curl_exec($ch );
		curl_close( $ch );

		$arr['msg']='success';
		echo json_encode($arr);
	}else{
		$arr['msg']='error';
		echo json_encode($arr);
	}
?>