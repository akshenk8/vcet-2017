<?php
#API access key from Google API's Console
    define( 'API_ACCESS_KEY', 'AAAA2hVs1-w:APA91bFqS9FeCdjKOlMSJp5g8RDkS7R6kxbbXBonlAfEbZeYo67mD-ZPWJ8EaZLpp0Xwa95qmkl73nKctCsOmxV9sQ_pEvcogGc_7U_yyl1_rW1psCBI5aFruV3NSSl_nQjcg6SIp_fJ' );
    $registrationIds = 'e5EbyYxTzAc:APA91bH9XorOuXFfTBhVASmFZIAWB-G5iApw9th_u46jh-0U-MSMgyT3C1OftR9zWHflYN8KY-D-dCcFYWH8yB5_CeVY3WuGb7egoLrDoBK21ln8sZiZe_8r4xuSy-fgXj_lGqacOzKm';
#prep the bundle
     $msg = array
          (
		'body' 	=> 'Body  Of Notification',
		'title'	=> 'Title Of Notification'
          );
	$fields = array
			(
				'to'		=> $registrationIds,
				'data'	=> $msg
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
#Echo Result Of FireBase Server
echo $result;