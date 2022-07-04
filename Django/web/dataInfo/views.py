#from wsgiref.util import request_uri
#from django.http import HttpResponse
#from django.http import JsonResponse
#from django.forms.models import model_to_dict
#from web.utils_mqtt import publish_message
#from django.shortcuts import render
from .models import dataInfo,rfidInfo,adminInfo
import json
from django.core import serializers

from rest_framework.views import APIView
from rest_framework.response import Response

class moniter(APIView):
    def post(self,request): #addInfo
        id=request.POST.get('adminid')
        adminpwd=request.POST.get('adminpwd')
        adminuid=request.POST.get('adminuid')
        adminname=request.POST.get('adminname')
        #adminphoto=request.POST.get('adminphoto')
        admininfo = adminInfo(adminid=id,adminpwd=adminpwd,
            adminuid=adminuid,adminname=adminname)
        #adminInfo.objects.update(adminid=id,adminpwd=adminpwd,
        #    adminuid=adminuid,adminname=adminname,adminphoto=adminphoto)
        #print(id)
        admininfo.save()
        #return HttpResponse()
        return Response()
    def get(self,request):
        if(int(request.query_params.get('table'))==1): #showdata
            #dataInfo.objects.create(humidity=12)
            offset=int(request.query_params.get('offset'))
            pagesize=int(request.query_params.get('pagesize'))
            data=dataInfo.objects.order_by('-id')[offset:pagesize]
            
            for obj in data:      
                json_data = serializers.serialize('json', data) 
                json_data = json.loads(json_data) 
                data = []
                for i in range(len(json_data)):
                    data.append(json_data[i]['fields']) # field:去掉不要的model和pk值
                return Response(data)
                #return JsonResponse(data, safe=False)
                #[{"temp": "35", "humi": "44", "illu": "23" }]
        elif(int(request.query_params.get('table'))==2): #showcheck
            offset=int(request.query_params.get('offset'))
            pagesize=int(request.query_params.get('pagesize'))
            data=rfidInfo.objects.order_by('-id')[offset:pagesize]
            
            for obj in data:      
                json_data = serializers.serialize('json', data) 
                json_data = json.loads(json_data) 
                data = []
                for i in range(len(json_data)):
                    data.append(json_data[i]['fields']) # field:去掉不要的model和pk值
                return Response(data)
                #return JsonResponse(data, safe=False)
        elif(int(request.query_params.get('table'))==3): #showinfo
            adminid=int(request.query_params.get('adminid'))
            data=adminInfo.objects.filter(adminid=adminid)
                
            json_data = serializers.serialize('json', data) 
            json_data = json.loads(json_data) 
            data = [] 
            for i in range(len(json_data)):
                data.append(json_data[i]['fields']) # field:去掉不要的model和pk值
            #return JsonResponse(data, safe=False)
            return Response(data)


    #   publish_message('1',response)
        #return HttpResponse(response)

    



   

