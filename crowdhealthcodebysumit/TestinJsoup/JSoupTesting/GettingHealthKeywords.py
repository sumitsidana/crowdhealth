'''
Created on Sep 23, 2014

@author: sumit
'''
from BeautifulSoup import BeautifulSoup
from BeautifulSoup import BeautifulStoneSoup
import BeautifulSoup

from BeautifulSoup import BeautifulSoup
import re
import urllib2

def drange2(start, stop, step):
    numelements = int((stop-start)/float(step))
    for i in range(numelements+1):
            yield start + i*step

uniqueFood = set()

for incrementby25 in drange2(0,8600,25):
   # print incrementby25
    page = urllib2.urlopen("http://ndb.nal.usda.gov/ndb/?format=&count=&max=25&sort=&fg=&man=&lfacet=&qlookup=&offset="+str(incrementby25))
    soup = BeautifulSoup(page)
    mydivs = soup("div", { "class" : "wbox" })
    incrementer = 0
    
    for body in mydivs:
        bodyoftable = body.findAll("tbody")
        for tabledata in bodyoftable:
            tdata = tabledata.findAll("td")
            for i in tdata: 
                j = i.findAll('a')
                for k in j:
                    incrementer = incrementer+1
                    if incrementer%2 == 0:
                        nutrient = k.string
                        fooditems = nutrient.split(",")
                        for food in fooditems:
                            #print(food)
                            uniqueFood.add(food)
                            
f = open("/media/windows/NutrientList/fooditems",'w')
for item in uniqueFood:
    f.write(item+"\n")
                        

    
                
            
