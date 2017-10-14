<?php
error_reporting(E_ALL);
// define('FIREBASE_API_KEY', "AAAA2hVs1-w:APA91bFqS9FeCdjKOlMSJp5g8RDkS7R6kxbbXBonlAfEbZeYo67mD-ZPWJ8EaZLpp0Xwa95qmkl73nKctCsOmxV9sQ_pEvcogGc_7U_yyl1_rW1psCBI5aFruV3NSSl_nQjcg6SIp_fJ");

define('FIREBASE_API_KEY','AIzaSyBzjTj-csGDYi-MoUbpIKeFbL7h4DsXhhs');

class Firebase {
        // sending push message to single user by firebase reg id        
        public function send($to, $message) {
            $fields = array(
                'to' => $to,
                'data' => $message,
            );
            return $this->sendPushNotification($fields);
        }        
        // function makes curl request to firebase servers
        private function sendPushNotification($fields) {
            // Set POST variables
            $url = 'https://fcm.googleapis.com/fcm/send';
            $headers = array(
                'Authorization: key=' . FIREBASE_API_KEY,
                'Content-Type: application/json'
            );
            // Open connection
            $ch = curl_init();
            // Set the url, number of POST vars, POST data
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            // Disabling SSL Certificate support temporarly
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
            
            // Execute post
            $result = curl_exec($ch);
            if ($result === FALSE) {
                die('Curl failed: ' . curl_error($ch));
            }
            // Close connection
            curl_close($ch);
            return $result;
        }
    }

        $f= new Firebase;
$f->send('eVX08igk_IA:APA91bFbgBPpvfTfbeR5JzZJjk_dotNY0RwFqrd_JWkm7LR5AvPyLyG5jNIW_4q8QG3EM1GtlDhiHAeYC9XJ14JRWUqpg_hiW_09ZO775xXxzWgj_TICoT8dlrT5Ee75OKJ3JHXa3_O3',"'msg':'hi'");
?>