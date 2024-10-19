EntrantDB(entrant_id(document_id):string,name:string,email:string,phone number:string,profile_photo:string,device:string,events_id<array<event_id:string,location:string>>)

OrganizerDB(organizer_id(document_id):string,name:string,email:string,phone number:string,device:string,events_id<array<event_id>>)

OverallDB(organizer_id(document_id):string,name:string,QRcode:string,poster_photo:string,facility:string,start_date:string,end_date,entrants:array<entrant_id:string>,organizer:array<organizer_id:string>)

FacilityDB(faiclity_id(document_id):string,location:string)

AdminDB(admind_id(document_id):string,name:string,email:string,phone number:string)

