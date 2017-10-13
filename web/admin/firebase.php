<?php
define('FIREBASE_API_KEY', "AAAAsoG0kkE:APA91bEv17JeNwBV3v1elFCAy_wtyG517cfMBv5jo-9dKCyAmQL1SykZFtGamRd-vAwpTmLWLu_8gyjnJ1bkIt-tU-9uNkVpJP1hwEwIRguysTjj-64BAFHSoXnqKSLUxNJEIgKaAPtQ");

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