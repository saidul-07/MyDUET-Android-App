-- ==============================================================================
-- MyDUET Android App - Supabase Database & Realtime Schema
-- ==============================================================================

-- 1. Create Events Table
CREATE TABLE IF NOT EXISTS public.events (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    type TEXT NOT NULL DEFAULT 'University', -- 'University' or 'Club'
    club_name TEXT,
    organizer_name TEXT,
    banner_url TEXT,
    event_date TEXT NOT NULL, -- YYYY-MM-DD
    start_time TEXT NOT NULL, -- HH:MM
    end_time TEXT NOT NULL, -- HH:MM
    venue TEXT NOT NULL,
    registration_required BOOLEAN DEFAULT FALSE,
    registration_deadline TEXT, -- YYYY-MM-DD HH:MM
    registration_url TEXT,
    contact_name TEXT,
    contact_email TEXT,
    contact_phone TEXT,
    max_participants INTEGER,
    social_media_url TEXT,
    additional_info TEXT,
    status TEXT DEFAULT 'Upcoming', -- 'Upcoming', 'Ongoing', 'Completed', 'Cancelled'
    created_by TEXT,
    created_at BIGINT DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT,
    updated_at BIGINT DEFAULT (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT
);

-- 2. Create Profiles Table (Linked with Supabase Auth)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    user_id TEXT UNIQUE NOT NULL, -- e.g. 'admin', 'duet_auth', 'club_auth'
    role TEXT NOT NULL DEFAULT 'CLUB_AUTHORITY', -- 'ADMIN', 'UNIVERSITY_AUTHORITY', 'CLUB_AUTHORITY'
    display_name TEXT NOT NULL
);

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- 4. RLS Policies for Events
-- Allow ANY student/anonymous user to read events:
DROP POLICY IF EXISTS "Public Read Events" ON public.events;
CREATE POLICY "Public Read Events" 
ON public.events FOR SELECT 
USING (true);

-- Allow authenticated authorities to insert events:
DROP POLICY IF EXISTS "Authenticated Insert Events" ON public.events;
CREATE POLICY "Authenticated Insert Events" 
ON public.events FOR INSERT 
TO authenticated 
WITH CHECK (true);

-- Allow authenticated authorities to update events:
DROP POLICY IF EXISTS "Authenticated Update Events" ON public.events;
CREATE POLICY "Authenticated Update Events" 
ON public.events FOR UPDATE 
TO authenticated 
USING (true);

-- Allow authenticated authorities to delete events:
DROP POLICY IF EXISTS "Authenticated Delete Events" ON public.events;
CREATE POLICY "Authenticated Delete Events" 
ON public.events FOR DELETE 
TO authenticated 
USING (true);

-- 5. RLS Policies for Profiles
DROP POLICY IF EXISTS "Public Read Profiles" ON public.profiles;
CREATE POLICY "Public Read Profiles" 
ON public.profiles FOR SELECT 
USING (true);

DROP POLICY IF EXISTS "Users Manage Own Profile" ON public.profiles;
CREATE POLICY "Users Manage Own Profile" 
ON public.profiles FOR ALL 
TO authenticated 
USING (auth.uid() = id);

-- 6. Enable Realtime Replication on Events Table
ALTER PUBLICATION supabase_realtime ADD TABLE public.events;

-- 7. Insert Initial Seed Events (Demo Data)
INSERT INTO public.events (
    title, description, type, club_name, organizer_name, banner_url,
    event_date, start_time, end_time, venue, registration_required,
    registration_deadline, registration_url, contact_name, contact_email,
    contact_phone, max_participants, social_media_url, additional_info,
    status, created_by
) VALUES 
(
    'DUET National Programming Contest',
    'Annual programming competition for university students across Bangladesh. Join us to compete for the championship title!',
    'Club',
    'CSE Programming Club',
    'CSE Dept & Programming Club',
    'https://images.unsplash.com/photo-1515187029135-18ee286d815b?q=80&w=600&auto=format&fit=crop',
    '2026-09-15',
    '09:00',
    '17:00',
    'CSE Computer Lab 3 & Auditorium',
    TRUE,
    '2026-09-14 23:59',
    'https://example.com/npc-register',
    'Prof. Dr. Raju Ahmed',
    'raju.ahmed@duet.ac.bd',
    '+8801713000001',
    200,
    'https://facebook.com/duet.cse.pc',
    'Participants should bring their ID cards and laptops.',
    'Upcoming',
    'club_auth'
),
(
    'Seminar on Generative AI & Career Prospects',
    'Discover the latest breakthroughs in artificial intelligence and how to prepare for careers in machine learning.',
    'University',
    '',
    'Department of CSE, DUET',
    'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?q=80&w=600&auto=format&fit=crop',
    '2026-09-20',
    '11:00',
    '13:30',
    'Central Auditorium, DUET',
    TRUE,
    '2026-09-19 18:00',
    'https://example.com/ai-seminar',
    'Dr. Md. Rafiqul Islam',
    'rafiqul@duet.ac.bd',
    '+8801713000002',
    150,
    'https://facebook.com/duet.official',
    'Open to all students from all departments.',
    'Upcoming',
    'duet_auth'
),
(
    'DUET Robotics Festival & Robo-Fight',
    'Annual flagship robotics challenge featuring line follower robots, soccer bots, and combat robot arenas.',
    'Club',
    'DUET Robotics Club',
    'DUET Robotics Society',
    'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?q=80&w=600&auto=format&fit=crop',
    '2026-09-28',
    '10:00',
    '18:00',
    'DUET Gymnasium',
    TRUE,
    '2026-09-26 23:59',
    'https://example.com/robotics-fest',
    'Engr. Tanvir Hasan',
    'robotics@duet.ac.bd',
    '+8801713000003',
    300,
    'https://facebook.com/duet.robotics',
    'Exciting prize pool for champions in 4 tracks.',
    'Upcoming',
    'club_auth'
);

