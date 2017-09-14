# PlanIt

![alt text](https://github.com/12tp12/PlanIt/blob/master/app/src/main/res/drawable/material_template_ic_product_icon_192px_light_dark_new.png)
 


PlanIt is an app for planning daily events such as camping, parties & etc.

## Top Links

1) [Video of PlanIt](https://www.youtube.com/watch?v=eyCMSAt-DJA) 
2) [Download App from Play Store](https://play.google.com/store/apps/details?id=com.planit.planit) 
3) [Presentation](https://docs.google.com/presentation/d/1-rLlBtGcNIZ8wPTa45vEu6MLh9XTPBADA4jaFkx_8cM/edit#slide=id.p24) 

### DB - Firebase Database Realtime

The DB that we used in PlanIt was Firebase (of google). 

### App Notifications Cloud Function

The code for the cloud functions is under  [Link](https://github.com/12tp12/PlanIt/tree/master/PlantItCF)
The app uses 4 cloud functions for notifications:
1) When date is updated - sends notifications to all friends in the specific event.
1) When time is updated - sends notifications to all friends in the specific event.
1) When location is updated - sends notifications to all friends in the specific event.
1) When new person is added to a event - sends notifications to all friends in the specific event.

## Screenshots

![alt text](https://github.com/12tp12/PlanIt/blob/master/app/src/main/res/drawable/device-2017-08-28-235501.png)
 

## Authors

* **Tomer Patel** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)
* **Dor Avrahami** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

## License


