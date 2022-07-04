"""web URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from dataInfo import views as info_view
from django.conf.urls.static import static
from . import settings

from dataInfo import views as rest_views
urlpatterns = [
    path('admin/', admin.site.urls),
    # path('show/',info_view.showdata),
    # path('show2/',info_view.showcheck),
    # path('add/',info_view.addInfo),
    # path('show3/',info_view.showInfo),
    path('API/v1.0/moniter/',rest_views.moniter.as_view()),
]
