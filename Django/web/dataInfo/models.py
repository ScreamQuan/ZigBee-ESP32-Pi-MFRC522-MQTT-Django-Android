from asyncio.windows_events import NULL
from distutils.command.upload import upload
from tkinter import CASCADE
from django.db import models

# Create your models here.
class dataInfo(models.Model):
    temperature=models.CharField(max_length=64,default=1)
    humidity=models.CharField(max_length=64,default=2)
    illumination=models.CharField(max_length=64,default=3)
    

class rfidInfo(models.Model):
    id=models.AutoField(primary_key=True)
    uid=models.CharField(max_length=64)
    time=models.CharField(max_length=64)
    times=models.CharField(max_length=64)

class adminInfo(models.Model):
    adminid=models.CharField(max_length=64,primary_key=True)
    adminpwd=models.CharField(max_length=64,default=123)
    adminuid=models.CharField(max_length=64,null=True, blank=True)
    adminname=models.CharField(max_length=64,null=True, blank=True)
    #adminphoto=models.CharField(max_length=255,null=True, blank=True,default=NULL)
    
