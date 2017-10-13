import pymysql
from random import randint
import json
import time
import datatime
db = pymysql.connect("localhost", "root", "asd123qwe", "bbank")

cur = db.cursor()
cur.execute("SELECT bid FROM `bank_details` where bid>3")

bgs=["OP","ON","AP","AN","BP","BN","ABP","ABN"]
cols=["WBC","RBC","FFP","PC","CRY"]
ts=time.time()
st= datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d %H:%M:%S')

for x in cur.fetchall():
	bid = x[0]
	cur.execute("insert into inventory values(%d,'','','','','')"%(bid))
	for c in cols:
		data ={}
		for k in bgs:
			data[k]=randint(0,20)
		data['last_updated']=st
		cur.execute('update `inventory` set `%s`=\'%s\' where bid=%d'%(c,json.dumps(data),bid))

db.commit()