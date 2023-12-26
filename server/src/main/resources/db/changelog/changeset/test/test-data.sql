insert into _user (tg_id) values (1);
insert into _user (tg_id) values (2);
insert into _user (tg_id) values (3);

insert into deck (owner, title) values (1, 'regular expressions');
insert into user_deck (user_tg_id, deck_id) values (1, 1);
insert into user_deck (user_tg_id, deck_id) values (2, 1);

insert into side (title, text)
values (
        'How to represent one sign from the set?',
        'Using this construction you can also get a negation: any symbol except those in the set'
       );

insert into side (title, text, image_link)
values (
        '[str] represents s, t or r',
        'In order to get negation of set you need to ^ after first square bracket: [^str] means any symbol except s, t or r',
        'https://i.ibb.co/F8YfChH/2023-11-19-17-24-36.png'
       );
insert into card (deck_id, question, answer) values (1, 1, 2);

insert into side (title)
values (
        'What signs mean zero or more repetitions / one or more repetitions?'
       );
insert into side (title, image_link)
values (
        '"*" - zero or more, "+" - one or more',
        'https://i.ibb.co/HFhCRL7/2023-11-19-17-31-06.png'
       );
insert into card (deck_id, question, answer) values (1, 3, 4);

insert into side (title, text)
values (
        'How to limit the number of repetition of previous sign (expression)?',
        'Repetition exactly n-times, repetition of previous sign at least n-times and at most m-times, repetition of a previous sign at least n-times.'
       );
insert into side (title, image_link)
values (
        '{x, y} construction',
        'https://i.ibb.co/wg1Rcxn/2023-11-19-17-38-26.png'
       );
insert into card (deck_id, question, answer) values (1, 5, 6);
