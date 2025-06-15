--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-06-15 17:20:53

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4930 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 16397)
-- Name: pola; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pola (
    id integer NOT NULL,
    uzytkownik_id integer NOT NULL,
    nazwa text NOT NULL,
    powierzchnia_ha double precision,
    lokalizacja text
);


ALTER TABLE public.pola OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16396)
-- Name: pola_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pola_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pola_id_seq OWNER TO postgres;

--
-- TOC entry 4931 (class 0 OID 0)
-- Dependencies: 219
-- Name: pola_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pola_id_seq OWNED BY public.pola.id;


--
-- TOC entry 222 (class 1259 OID 16411)
-- Name: uprawy; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.uprawy (
    id integer NOT NULL,
    pole_id integer NOT NULL,
    nazwa text NOT NULL,
    data_siewu date,
    data_zbioru date,
    zysk double precision
);


ALTER TABLE public.uprawy OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16410)
-- Name: uprawy_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.uprawy_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.uprawy_id_seq OWNER TO postgres;

--
-- TOC entry 4932 (class 0 OID 0)
-- Dependencies: 221
-- Name: uprawy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.uprawy_id_seq OWNED BY public.uprawy.id;


--
-- TOC entry 218 (class 1259 OID 16388)
-- Name: uzytkownicy; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.uzytkownicy (
    id integer NOT NULL,
    login text NOT NULL,
    password character varying(255) NOT NULL
);


ALTER TABLE public.uzytkownicy OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16387)
-- Name: uzytkownicy_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.uzytkownicy_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.uzytkownicy_id_seq OWNER TO postgres;

--
-- TOC entry 4933 (class 0 OID 0)
-- Dependencies: 217
-- Name: uzytkownicy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.uzytkownicy_id_seq OWNED BY public.uzytkownicy.id;


--
-- TOC entry 224 (class 1259 OID 16425)
-- Name: zabiegi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zabiegi (
    id integer NOT NULL,
    uprawa_id integer NOT NULL,
    nazwa text NOT NULL,
    typ text,
    data date,
    dawka text,
    koszt double precision,
    rodzaj_zabiegu character varying(50),
    zarobek real
);


ALTER TABLE public.zabiegi OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16424)
-- Name: zabiegi_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.zabiegi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.zabiegi_id_seq OWNER TO postgres;

--
-- TOC entry 4934 (class 0 OID 0)
-- Dependencies: 223
-- Name: zabiegi_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.zabiegi_id_seq OWNED BY public.zabiegi.id;


--
-- TOC entry 4758 (class 2604 OID 16400)
-- Name: pola id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pola ALTER COLUMN id SET DEFAULT nextval('public.pola_id_seq'::regclass);


--
-- TOC entry 4759 (class 2604 OID 16414)
-- Name: uprawy id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.uprawy ALTER COLUMN id SET DEFAULT nextval('public.uprawy_id_seq'::regclass);


--
-- TOC entry 4757 (class 2604 OID 16391)
-- Name: uzytkownicy id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.uzytkownicy ALTER COLUMN id SET DEFAULT nextval('public.uzytkownicy_id_seq'::regclass);


--
-- TOC entry 4760 (class 2604 OID 16428)
-- Name: zabiegi id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zabiegi ALTER COLUMN id SET DEFAULT nextval('public.zabiegi_id_seq'::regclass);


--
-- TOC entry 4920 (class 0 OID 16397)
-- Dependencies: 220
-- Data for Name: pola; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pola (id, uzytkownik_id, nazwa, powierzchnia_ha, lokalizacja) FROM stdin;
9	6	Pole 1	10.5	Rzeszów
\.


--
-- TOC entry 4922 (class 0 OID 16411)
-- Dependencies: 222
-- Data for Name: uprawy; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.uprawy (id, pole_id, nazwa, data_siewu, data_zbioru, zysk) FROM stdin;
13	9	Uprawa 1	2025-06-13	2025-06-15	4750
\.


--
-- TOC entry 4918 (class 0 OID 16388)
-- Dependencies: 218
-- Data for Name: uzytkownicy; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.uzytkownicy (id, login, password) FROM stdin;
6	123	123
\.


--
-- TOC entry 4924 (class 0 OID 16425)
-- Dependencies: 224
-- Data for Name: zabiegi; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.zabiegi (id, uprawa_id, nazwa, typ, data, dawka, koszt, rodzaj_zabiegu, zarobek) FROM stdin;
26	13	Zabieg 1	Zabieg	2025-06-15	\N	250	ZBIÓR	5000
\.


--
-- TOC entry 4935 (class 0 OID 0)
-- Dependencies: 219
-- Name: pola_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pola_id_seq', 9, true);


--
-- TOC entry 4936 (class 0 OID 0)
-- Dependencies: 221
-- Name: uprawy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.uprawy_id_seq', 13, true);


--
-- TOC entry 4937 (class 0 OID 0)
-- Dependencies: 217
-- Name: uzytkownicy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.uzytkownicy_id_seq', 6, true);


--
-- TOC entry 4938 (class 0 OID 0)
-- Dependencies: 223
-- Name: zabiegi_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.zabiegi_id_seq', 26, true);


--
-- TOC entry 4764 (class 2606 OID 16404)
-- Name: pola pola_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pola
    ADD CONSTRAINT pola_pkey PRIMARY KEY (id);


--
-- TOC entry 4766 (class 2606 OID 16418)
-- Name: uprawy uprawy_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.uprawy
    ADD CONSTRAINT uprawy_pkey PRIMARY KEY (id);


--
-- TOC entry 4762 (class 2606 OID 16395)
-- Name: uzytkownicy uzytkownicy_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.uzytkownicy
    ADD CONSTRAINT uzytkownicy_pkey PRIMARY KEY (id);


--
-- TOC entry 4768 (class 2606 OID 16432)
-- Name: zabiegi zabiegi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zabiegi
    ADD CONSTRAINT zabiegi_pkey PRIMARY KEY (id);


--
-- TOC entry 4769 (class 2606 OID 16405)
-- Name: pola pola_uzytkownik_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pola
    ADD CONSTRAINT pola_uzytkownik_id_fkey FOREIGN KEY (uzytkownik_id) REFERENCES public.uzytkownicy(id);


--
-- TOC entry 4770 (class 2606 OID 16419)
-- Name: uprawy uprawy_pole_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.uprawy
    ADD CONSTRAINT uprawy_pole_id_fkey FOREIGN KEY (pole_id) REFERENCES public.pola(id);


--
-- TOC entry 4771 (class 2606 OID 16433)
-- Name: zabiegi zabiegi_uprawa_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zabiegi
    ADD CONSTRAINT zabiegi_uprawa_id_fkey FOREIGN KEY (uprawa_id) REFERENCES public.uprawy(id);


-- Completed on 2025-06-15 17:20:53

--
-- PostgreSQL database dump complete
--

