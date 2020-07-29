INSERT INTO users (date_of_registration, email, email_notification, name, last_visit, role, user_status, refresh_token_key)
VALUES ('2019-09-03 11:42:37.823000', 'nazar.stasyuk@gmail.com', 0, 'Назар Стасюк', '2019-09-03 11:42:37.823000', 0, 2, 'a06b0b4e-8696-419c-a776-611c36f79d39'),
       ('2019-09-03 12:17:18.345000', 'dovgal.dmytr@gmail.com', 0, 'Dima Dovhal', '2019-09-03 12:17:18.346000', 0, 2, 'f9aef11e-2bfd-4136-aafd-e0c7b92f396a'),
       ('2019-09-04 13:20:31.755000', 'rapac@clockus.ru', 0, 'Paul Kos', '2019-09-04 13:20:31.757000', 1, 2, '6f9d42f5-2324-492e-8d99-75e5834aa739'),
       ('2019-09-04 13:38:44.518000', 'rsssac@clockus.ru', 0, 'Roman KOcak', '2019-09-04 13:38:44.518000', 0, 2, 'b3ad7c1d-a058-40ee-908c-65902ef96371'),
       ('2019-09-04 13:39:25.827000', 'rsssasssc@clockus.ru', 0, 'Misha Pavluv', '2019-09-04 13:39:25.827000', 0, 2, '8171edb1-3a9c-4074-a305-fcdc24de470f'),
       ('2019-09-04 13:40:21.531000', '1warsssasssc@clockus.ru', 0, 'Taras Tymkiv', '2019-09-04 13:40:21.531000', 0, 2, 'c7e5ea40-610a-4908-8085-c3a96127e38d'),
       ('2019-09-04 13:43:00.061000', '1warssssssssasssc@clockus.ru', 0, 'Ihor Zdebskiy', '2019-09-04 13:43:00.061000', 0, 2, 'dba595ab-6d07-45a7-8f32-2e7956e68e46'),
       ('2019-09-04 13:44:08.939000', '1warssssssssassssc@clockus.ru', 0, 'Amon Azarov', '2019-09-04 13:44:08.939000', 0, 2, 'c3d9f0ed-d639-4e00-9928-07b7afe3d779'),
       ('2019-09-04 14:04:14.931000', 'rostuk.khasanov@gmail.com', 0, 'Rostyslav Khasanov', '2019-09-04 14:04:14.931000', 1, 2, '4238c90e-ea52-4331-8b62-be2511f3ac09'),
       ('2019-10-01 19:22:25.209000', 'milanmarian@mail.ru', 0, 'Marian Milian', '2019-10-01 19:22:25.209000', 1, 2, '16a02c58-915f-4a7d-b69b-2d86038fa66f'),
       ('2019-10-01 19:22:25.209000', 'milan@mail.ru', 0, 'M M', '2019-10-01 19:22:25.209000', 1, 2, '827947d3-da8b-4c66-a290-720e6c111531'),
       ('2019-10-01 19:22:25.209000', 'greencity448@gmail.com', 0, 'greencity 448', '2019-10-01 19:22:25.209000', 1, 2, '91e9d3f7-f92a-4557-9f5b-462133fa1090');

INSERT INTO own_security (password, user_id)
VALUES ('$2a$10$mAzH0BvVs/g2m6zl8CYxDuiLCu1V.PZjuGhKzHRdVxndDfExts4oO', 1),
       ('$2a$10$HqJME/hE.0THMpGbzBci5usUe9T7t4dfLyL./JbndpWgGhjD2qyqC', 2),
       ('$2a$10$Mdu2vmDtmjgATCh0EpZ6V.Q3uhJn5Kz4biDu.Ol3EX55Pv4D7Ltla', 3),
       ('$2a$10$5ms4Ni.xuIfSXp1RxScQjOlQvLbrCUlZUNNtkQm23jc99NQXkppMe', 4),
       ('$2a$10$QCM1BwKCmM2GCSeK6pTpOeaXrcYOLCleifvrytgwgB3FnoE497dV.', 5),
       ('$2a$10$bQggp0SIPwHh5D/ahmm4reKJtsod6dcEo79WJBO0aIAUIs/j9JGrC', 6),
       ('$2a$10$v.k/53rC6NnIsosZEH0ezecuKWrG8fL4yOHXW5w3AudFeFUub0It2', 7),
       ('$2a$10$hSKDmeUboTyvBpUnXj8c2ulIXBeHq5rd4h.H0Oj8gpzYlwS0L78qO', 8),
       ('$2a$10$cgaNMFjdAFX6k810YSZDSuvKVlWFcq6/F7p2lcgLzK6sFB48dky2W', 9),
       ('$2a$10$uptb7Z/KAiv9Bi5iaMPOHer4ScyDaiSVNR33eUcGqTjDHQ9twdmOS', 10),
       ('$2a$10$uptb7Z/KAiv9Bi5iaMPOHer4ScyDaiSVNR33eUcGqTjDHQ9twdmOS', 11),
       ('$2a$10$MwSE7uTZuKhCOjRddnpZ6eXiJT7sE3n8YsiHDJCo9MCv3yvy64Cf6', 12);

INSERT INTO categories (name)
VALUES ('Food');

INSERT INTO locations (address, lat, lng)
VALUES ('вулиця Під Дубом', 49.84988, 24.022533),
       ('Вулиця Кульпарківська, 226а, Львів, Львівська область, 79000', 49.807129, 23.977985),
       ('Площа Ринок, Львів, Львівська область, 79000', 49.842042, 24.030359),
       ('Проспект В''ячеслава Чорновола, 2, Львів, Львівська область, 79000', 49.847489, 24.025975),
       ('Площа Ринок, 14 (підвал), Львів, Львівська область, 79000', 49.841311, 24.03229);

INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
VALUES ('Forum', '0322 489 850', 'forum_lviv@gmail.com', '2004-05-23T14:25:10', 2, 1, 1, 'Shopping center', 1),
       ('Victoria Gardens', '0322 590 202', 'victoria_gardens@gmail.com', '2005-05-23T14:25:10', 2, 1, 1, 'Shopping center', 2),
       ('Pravda', '0322 157 694', 'pravda_lviv@gmail.com', '2016-09-23T14:25:10', 2, 1, 1, 'Restaurant', 3),
       ('Malevych', '0322 849 348', 'malevych_lviv@gmail.com', '2011-08-23T14:25:10', 2, 1, 1, 'Restaurant', 4),
       ('Kryivka', '067 310 3145', 'kryivka_lviv@gmail.com', '2009-07-23T14:25:10', 2, 1, 1, 'Restaurant', 5);

INSERT INTO specifications (name)
VALUES ('Animal'),
       ('Own cup'),
       ('Karaoke'),
       ('Shopping'),
       ('Ukrainian food'),
       ('Dance');

INSERT INTO favorite_places (place_id, user_id, name)
VALUES (1, 1, 'Forum'),
       (2, 2, 'Victoria Gardens'),
       (3, 3, 'Pravda');

INSERT INTO estimates (rate, place_id, user_id)
VALUES (5, 1, 1),
       (4, 2, 2),
       (2, 3, 1),
       (3, 4, 2),
       (5, 5, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 1),
       ('20:00:00', '06:00:00', 1, 1),
       ('20:00:00', '06:00:00', 2, 1),
       ('20:00:00', '06:00:00', 3, 1),
       ('20:00:00', '06:00:00', 4, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 2),
       ('20:00:00', '06:00:00', 1, 2),
       ('20:00:00', '06:00:00', 2, 2),
       ('20:00:00', '06:00:00', 3, 2),
       ('20:00:00', '06:00:00', 4, 2);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 3),
       ('20:00:00', '06:00:00', 1, 3),
       ('20:00:00', '06:00:00', 2, 3),
       ('20:00:00', '06:00:00', 3, 3),
       ('20:00:00', '06:00:00', 4, 3);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 4),
       ('20:00:00', '06:00:00', 1, 4),
       ('20:00:00', '06:00:00', 2, 4),
       ('20:00:00', '06:00:00', 3, 4),
       ('20:00:00', '06:00:00', 4, 4);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 5),
       ('20:00:00', '06:00:00', 1, 5),
       ('20:00:00', '06:00:00', 2, 5),
       ('20:00:00', '06:00:00', 3, 5),
       ('20:00:00', '06:00:00', 4, 5);

INSERT INTO discount_values(id, value, place_id, specification_id)
VALUES (1, 3, 1, 1),
       (2, 13, 2, 2),
       (3, 33, 3, 3),
       (4, 63, 4, 4),
       (5, 93, 5, 5),
       (6, 50, 5, 6);

INSERT INTO habit_dictionary (image)
VALUES ('bag'),
       ('cap');

INSERT INTO languages(code)
VALUES ('uk'),
       ('en'),
       ('ru');

INSERT INTO habit_dictionary_translation(name, description, habit_item, language_id, habit_dictionary_id)
VALUES ('Економити пакети', 'Опис пакетів', 'Пакети', 1, 1),
       ('Save bags', 'bag description', 'bags', 2, 1),
       ('экономить пакеты', 'описание пакетов', 'Пакеты', 3, 1),
       ('Відмовитись від одноразових стаканчиків', 'Опис стаканчиків', 'Стаканчики',1, 2),
       ('Discard disposable cups', 'cap description', 'caps',2, 2),
       ('Отказаться от одноразовых стаканчиков', 'описание стаканчиков', 'Стаканчики',3, 2);

INSERT INTO habits (user_id, habit_dictionary_id, status, create_date)
VALUES (1, 1, true, '2019-11-12 19:03:33'),
       (1, 2, true, '2019-11-12 15:12:59'),
       (2, 1, true, '2019-11-14 11:33:01'),
       (2, 2, true, '2019-11-15 20:01:19'),
       (3, 1, true, '2019-11-15 10:21:11'),
       (3, 2, true, '2019-11-16 17:01:09'),
       (4, 1, true, '2019-11-20 19:11:51'),
       (4, 2, true, '2019-11-20 21:12:52');

INSERT INTO habit_statistics(rate, date, amount_of_items, habit_id)
VALUES ('GOOD', '2019-11-13', 12, 1),
       ('NORMAL', '2019-11-14', 9, 2),
       ('BAD', '2019-11-14', 2, 3),
       ('NORMAL', '2019-11-15', 5, 4),
       ('NORMAL', '2019-11-15', 7, 5),
       ('GOOD', '2019-11-16', 14, 6),
       ('NORMAL', '2019-11-16', 7, 7),
       ('GOOD', '2019-11-16', 15, 8);


INSERT INTO goals(id)
VALUES (1),
       (2),
       (3),
       (4),
       (5);

INSERT INTO custom_goals(text, user_id)
VALUES ('Buy a bamboo brush', 1),
       ('Buy composter', 1),
       ('Start sorting trash', 2),
       ('Start recycling batteries', 2),
       ('Finish book about vegans', 3);

INSERT INTO advices(habit_dictionary_id)
VALUES (1),
       (2),
       (1),
       (1),
       (1),
       (1);

INSERT INTO habit_facts (habit_dictionary_id)
VALUES (1),
       (2),
       (2),
       (2),
       (2),
       (1),
       (1),
       (1);

INSERT INTO goal_translations(text, goal_id, language_id)
VALUES ('Купіть бамбукову щітку', 1, 1),
       ('Buy a bamboo brush', 1, 2),
       ('Купите бамбуковую щетку', 1, 3),
       ('Купіть компостер', 2, 1),
       ('Buy composter', 2, 2),
       ('Купить компостер', 2, 3),
       ('Почніть сортувати сміття', 3, 1),
       ('Start sorting trash', 3, 2),
       ('Начните сортировать мусор', 3, 3),
       ('Почніть переробляти батарейки', 4, 1),
       ('Start recycling batteries', 4, 2),
       ('Начните перерабатывать батарейки', 4, 3),
       ('Прочитайте книгу про вегетаріанство', 5, 1),
       ('Finish book about vegans', 5, 2),
       ('Прочитайте книгу о вегетарианстве', 5, 3);


INSERT INTO advice_translations(language_id, advice_id, content)
VALUES (1, 1, 'Покладіть по одній еко-сумці в кожну сумку чи рюкзак, так вона завжди буде з вами, якщо ви несподівано вирушите в магазин.'),
       (2, 1, 'Put one eco-bag in each bag or backpack, so it will always be with you if you unexpectedly go to the store.'),
       (3, 1, 'Положите по одной эко-сумке в каждую сумку или рюкзак, чтобы она всегда была с вами, если вы неожиданно отправитесь в магазин.'),
       (1, 2, 'Не беріть стаканчики - бережіть природу.'),
       (2, 2, 'Don''t take a glass - protect nature.'),
       (3, 2, 'Не берите стаканчики - берегите природу.'),
       (1, 3, 'Після переходу на сумки для багаторазового використання вам більше не доведеться постійно купувати пакети для товарів і продуктів. Еко сумки мають довший термін експлуатації та зношуються повільно.'),
       (2, 3, 'After switching to reusable bags, you no longer have to constantly buy packages for goods and products. Eco bags have a longer life and wear out slowly.'),
       (3, 3, 'После перехода на многоразовые сумки вам больше не придется постоянно покупать пакеты для товаров и продуктов. Эко-сумки имеют более длительный срок службы и медленно изнашиваются.'),
       (1, 4, 'Почніть зменшення побутових відходів з відмови від пластикових пакетів.'),
       (2, 4, 'To reduce household waste, start by giving up plastic bags.'),
       (3, 4, 'Начните уменьшения бытовых отходов с отказа от пластиковых пакетов.'),
       (1, 5, 'Всі сумки для багаторазового використання дуже легко чистити. Не потрібно турбуватися про те, що варення, мед або крихта потрапляють в куточки. Просто покладіть сумку в пральну або посудомийну машину.'),
       (2, 5, 'All reusable bags are very easy to clean. There is no need to worry about jam, honey or crumbs falling into the corners. Just put the bag in the washing machine or dishwasher.'),
       (3, 5, 'Все сумки для многократного использования очень легко чистить. Не нужно беспокоиться о том, что варенье, мед или крошка попадают в уголки. Просто положите сумку в стиральную или посудомоечную машину.'),
       (1, 6, 'Еко-сумки дуже популярні серед спортсменів і всіх, хто веде здоровий спосіб життя.'),
       (2, 6, 'Eco Bags are very popular with athletes and anyone who leads a healthy lifestyle.'),
       (3, 6, 'Эко-сумки очень популярны у спортсменов и тех, кто ведет здоровый образ жизни.');

INSERT INTO fact_translations(language_id, habit_fact_id, content)
VALUES (1, 1, 'Покладіть  до кожної сумки чи рюкзаку одну еко-сумку, так вона буде завжди з вами, якщо ви неочікувано зайдете в магазин.'),
       (2, 1, 'Put one eco-bag in each bag or backpack, so it will always be with you if you unexpectedly go to the store.'),
       (3, 1, 'Положите в каждой сумки или рюкзаке одну эко-сумку, так она будет всегда с вами, если вы неожиданно зайдете в магазин.'),
       (1, 2, 'Більшість шкідливих речовин містяться навіть не в стаканчиках, а в кришечках та трубочках. Їх переважно виготовляють з 6-го або 7-го типів пластику. Вони виділяють токсичну речовину — Дисанол-А.'),
       (2, 2, 'Most harmful substances are not even contained in cups but in lids and tubes. They are preferably made of 6 or 7 types of plastic. They secrete a toxic substance - Disanol-A.'),
       (3, 2, 'Большинство вредных веществ содержатся даже не в стаканчиках, а в крышечках и трубочках. Их преимущественно изготавливают из 6-го или 7-го типов пластика. Они выделяют токсическое вещество - Дисанол-А.'),
       (1, 3, 'На заводі зі стаканчиків знімається плівка, і картон вдається переробити. Але наразі виникають проблеми з логістикою між Львовом та Харківською областю. На це поки що немає фінансів'),
       (2, 3, 'At the factory, the film is removed from the cups and the cardboard can be recycled. But now there are problems with logistics between Lviv and Kharkiv region. There is no finance for this yet'),
       (3, 3, 'На заводе по стаканчиков снимается пленка, и картон удается переделать. Но пока возникают проблемы с логистикой между Львовом и Харьковской области. На это пока нет денег'),
       (1, 4, 'Сміттєспалювального заводу, який би утилізовував небезпечні стаканчики, в Україні поки що не збудували'),
       (2, 4, 'A waste incineration plant that would dispose of hazardous cups in Ukraine has not yet been built'),
       (3, 4, 'Мусоросжигательного завода, который бы утилизовував опасные стаканчики, в Украине пока не построили'),
       (1, 5, 'Культура кави з собою дуже глибоко вкорінилася в нашому житті, і через це щодня тисячі паперових стаканчиків опиняються на смітнику.'),
       (2, 5, 'The coffee culture is deeply rooted in our lives, and because of this, thousands of paper cups are thrown into the dump every day.'),
       (3, 5, 'Культура кофе с собой очень глубоко укоренилась в нашей жизни, и поэтому каждый день тысячи бумажных стаканчиков оказываются на свалке.'),
       (1, 6, 'Одноразові пакети на касі в супермаркетах коштують 2-3 гривні. Вони не тільки забруднюють навколишнє середовище, а ще й з’їдають частину вашого бюджету. Набагато практичніше носити з собою складну сумочку з тканини, ще зовсім недавно так робили всі люди.'),
       (2, 6, 'Disposable packages at checkout in supermarkets cost 2-3 hryvnias. Not only do they pollute the environment, they also eat up part of your budget. It is much more practical to carry a sophisticated fabric bag with you, as recently as all people have done.'),
       (3, 6, 'Одноразовые пакеты на кассе в супермаркетах стоят 2-3 гривны. Они не только загрязняют окружающую среду, но и съедают часть вашего бюджета. Гораздо практичнее носить с собой сложную сумочку из ткани, еще совсем недавно так делали все люди.'),
       (1, 7, 'За даними ООН, приблизно 5 трильйонів поліетиленових пакетів споживаються у світі щороку, або близько 10 мільйонів - щохвилини.'),
       (2, 7, 'According to the UN, about 5 trillion plastic bags are consumed in the world each year, or about 10 million every minute.'),
       (3, 7, 'По данным ООН, примерно 5000000000000 полиэтиленовых пакетов потребляются в мире ежегодно, или около 10000000 - ежеминутно.'),
       (1, 8, 'Наразі, близько 60 країн світу почали боротьбу із пластиком на законодавчому рівні.'),
       (2, 8, 'Currently, around 60 countries have begun to fight plastic at the legislative level.'),
       (3, 8, 'Сейчас около 60 стран мира начали борьбу с пластиком на законодательном уровне.');

INSERT INTO user_goals(user_id, goal_id, custom_goal_id, status, date_completed)
VALUES (1, 1, null, 'ACTIVE', null),
       (2, 3, null, 'ACTIVE', null),
       (2, 4, null, 'DONE', '2019-11-14 19:04:51'),
       (1, null, 1, 'DONE', '2019-11-15 12:44:36'),
       (3, null, 2, 'ACTIVE', null),
       (3, null, 3, 'DONE', '2019-11-11 13:55:13');

INSERT INTO achievements(title, description, message)
VALUES ('Acquaintance', 'Register and pass onboarding', 'Welcome you in Green City application!'),
       ('Eco friend', 'Start to track a habit',
        'Good start for you and do not give up! Achievement "Eco friend" is yours.'),
       ('Сonscious', 'Made first habit',
        'Congratulation! You made your first eco habit and you deserve achievement "Сonscious".'),
       ('Wow', 'Add second habit', 'It looks you want to track one more habit. It is brilliant idea!'),
       ('Well done', 'Well done. Keep doing your goals with the same enthusiasm', 'Complete 3 goals!'),
       ('ach6', 'get ach 6', 'info'),
       ('ach7', 'get ach 7', 'info'),
       ('ach8', 'get ach 8', 'info'),
       ('ach9', 'get ach 9', 'info');

INSERT INTO tags(name)
VALUES ('News'),
       ('Events'),
       ('Education'),
       ('Initiatives'),
       ('Ads'),
       ('Lifehacks'),
       ('Green thinking'),
       ('Zero waste'),
       ('3 r''s'),
       ('Eco-city');

INSERT INTO eco_news(creation_date, image_path, author_id, text, title)
VALUES ('2020-04-11 18:33:51', 'шлях до картинки', 1,
        'No matter where you live , you can make a difference in the impact of big agriculture. Purchasing foods produced by small, local farms, opting for organic produce whenever possible',
        'A New Way To Buy Food'),
       ('2020-04-11 18:55:18', 'шлях до картинки', 2,
        'The benefits of biodegradable substances are only felt when they are disposed of properly. Compost piles capture and return all of the recycled nutrients to the environment, and help to sustain new life. ',
        'Why Biodegradable Products are Better for the Planet'),
       ('2020-04-11 19:06:36', 'шлях до картинки', 3,
        'Over six gallons of water are required to produce one gallon of wine.',
        'Sustainable Wine Is Less Damaging to the Environment, But How Can You Spot It?'),
       ('2020-04-11 19:14:15', 'шлях до картинки', 4,
        'Instead of trying to get rid of those lawn and garden weeds, harvest them for free homegrown meals.',
        'Please eat the dandelions: 9 edible garden weeds'),
       ('2020-04-11 19:22:57', 'шлях до картинки', 5,
        'Weather happens hour by hour, day by day—it''s a thunderstorm, a heat wave, a windy afternoon. Taken as averages over decades and centuries, those patterns of precipitation, temperature, and wind for a given region comprise our climate.',
        'Climate Change'),
       ('2020-04-11 19:31:35', 'шлях до картинки', 6,
        'According to the Environmental Protection Agency, food waste in the United States has tripled since 1960. In landfills, its decomposition generates methane, a potent greenhouse gas.',
        'A Growing Problem'),
       ('2020-04-11 19:44:19', 'шлях до картинки', 7,
        'Global warming — a component of climate change — is the rapid increase in recorded temperatures of the ocean, land, and air caused by rising levels of carbon dioxide and other greenhouse gases in the atmosphere.',
        'Global Warming'),
       ('2020-04-11 19:50:56', 'шлях до картинки', 8,
        'Researchers have found evidence of rainforests near the South Pole 90 million years ago, suggesting the climate was exceptionally warm at the time',
        'Traces of ancient rainforest in Antarctica point to a warmer prehistoric world'),
       ('2020-04-11 19:55:05', 'шлях до картинки', 9, 'Rising sea surface temperatures and acidic waters could eliminate nearly all existing coral reef habitats by 2100, suggesting restoration projects in these areas will likely meet serious challenges','Warming, acidic oceans may nearly eliminate coral reef habitats by 2100'),
       ('2020-04-11 20:12:56', 'шлях до картинки', 10, 'Four fossilized monkey teeth discovered deep in the Peruvian Amazon provide new evidence that more than one group of ancient primates journeyed across the Atlantic Ocean from Africa.', 'Ancient Teeth from Peru Hint Now-Extinct Monkeys Crossed Atlantic from Africa'),
       ('2020-04-11 20:15:03', 'шлях до картинки', 11, 'Researchers from Cambridge University and University of California San Diego have 3D printed coral-inspired structures that are capable of growing dense populations of microscopic algae', '3D-printed corals could improve bioenergy and help coral reefs');

INSERT INTO eco_news_tags(eco_news_id, tags_id)
VALUES (1, 4),
       (2, 4),
       (3, 1),
       (4, 4),
       (5, 1),
       (6, 1),
       (7, 1),
       (8, 1),
       (9, 1),
       (10, 1),
       (11, 1);

INSERT INTO tips_and_tricks(title, text, creation_date, author_id, image_path, source)
VALUES ('Don''t take home any unnecessary trash or junk',
        'If you want a flyer or business card, take a photo rather than taking it home. This is especially relevant at concerts and outings where freebies are being passed out.',
        '2020-05-11 18:55:18', 1, 'image path', 'https://sweetsimplevegan.com/2019/04/25-ways-to-reduce-waste/'),
       ('Go paperless',
        'We know this can be a challenge, but opting for kindles, online books, or even renting from the library or borrowing from a friend makes a huge difference.',
        '2020-05-12 18:55:18', 2, 'image path', 'https://sweetsimplevegan.com/2019/04/25-ways-to-reduce-waste/'),
       ('Use reusable storage bags instead of ziplock bags',
        'Say goodbye to plastic ziplock bags for good. Reusable storage bags are the perfect alternative to single-use plastic bags and are so much more versatile.',
        '2020-05-13 18:55:18', 3, 'image path', 'https://sweetsimplevegan.com/2019/04/25-ways-to-reduce-waste/');

INSERT INTO tips_and_tricks_tags(tips_and_tricks_id, tags_id)
VALUES (1, 6),
       (1, 8),
       (1, 10),
       (2, 8),
       (3, 6),
       (3, 7)