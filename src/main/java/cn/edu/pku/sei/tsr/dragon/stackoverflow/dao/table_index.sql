alter table answers add index answers_parentid_index (ParentId);
alter table comments add index comments_postid_index (PostId);
alter table tags add index tags_questionid_index (questionId);
alter table votes add index votes_postid_index (PostId);
alter table posthistory add index posthistory_postid_index (PostId);