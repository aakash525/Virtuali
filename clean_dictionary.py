html_symbols = ["&bull;","&lt;","&gt;","&amp;","&nbsp;","&rsquo;","/m\u00b2","\r","\n","\t", "\u00" ]

import json
dictionary_to_work= json.load(open("medicine_database.json", "r"))
dictionary_to_work = dictionary_to_work["medicine"]

#temp = "hey i am | bndj"
#e = temp.split("|")abaloparatide
#print e
for key in dictionary_to_work.keys():
#    print key
    medicine_dict = dictionary_to_work[key]

    for inner_key in medicine_dict:
        
        print "yeah"
#        print inner_key
#        string with which data needs t be cleared
        temp = medicine_dict[inner_key]
        print temp
        for html_symbol in html_symbols:
            print html_symbol
#            print html_symbol
            temp = temp.split(html_symbol)
            print"temp",temp
            tem = " ".join(temp)
            print "join krne ke bad", tem
            temp = tem
        medicine_dict[inner_key]= temp
            
                    
                    
#            print temp
json.dump(dictionary_to_work, open("clean_medicine_database.json","w"),indent=4, sort_keys=True)
#    
            
    
    
#?? read each key and its value
#split it into tokens where ever