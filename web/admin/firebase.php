<?php
define('FIREBASE_API_KEY', "AAAA2hVs1-w:APA91bFqS9FeCdjKOlMSJp5g8RDkS7R6kxbbXBonlAfEbZeYo67mD-ZPWJ8EaZLpp0Xwa95qmkl73nKctCsOmxV9sQ_pEvcogGc_7U_yyl1_rW1psCBI5aFruV3NSSl_nQjcg6SIp_fJ");

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

?>