import React, { useState, useEffect } from 'react';
import Navbar from '../components/common/Navbar';
import HeroSection from '../components/landing/HeroSection';
import SpecializationsGrid from '../components/landing/SpecializationsGrid';
import DoctorFinder from '../components/landing/DoctorFinder';
import HowItWorks from '../components/landing/HowItWorks';
import Footer from '../components/landing/Footer';
import BookingModal from '../components/booking/BookingModal';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function LandingPage() {
  const [specializations, setSpecializations] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [selectedSpecialization, setSelectedSpecialization] = useState(null);
  const [bookingDoctor, setBookingDoctor] = useState(null);
  const [isBookingModalOpen, setIsBookingModalOpen] = useState(false);

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [specRes, docRes] = await Promise.all([
        api.get('/specializations'),
        api.get('/doctors/directory'),
      ]);
      setSpecializations(specRes.data);
      setDoctors(docRes.data);
    } catch (err) {
      console.error('Error fetching landing page data:', err);
    }
  };

  const handleBookDoctor = (doc) => {
    if (!user) {
      navigate('/login');
      return;
    }
    setBookingDoctor(doc);
    setIsBookingModalOpen(true);
  };

  const handleGeneralBookClick = () => {
    // Scroll down to doctor finder or open first doctor
    if (doctors.length > 0) {
      handleBookDoctor(doctors[0]);
    } else {
      const docSec = document.getElementById('doctors');
      if (docSec) docSec.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Navbar />

      <main className="flex-grow">
        <HeroSection onBookClick={handleGeneralBookClick} />

        <SpecializationsGrid
          specializations={specializations}
          selectedSpecialization={selectedSpecialization}
          onSelectSpecialization={setSelectedSpecialization}
        />

        <DoctorFinder
          doctors={doctors}
          specializations={specializations}
          selectedSpecialization={selectedSpecialization}
          onSelectSpecialization={setSelectedSpecialization}
          onBookDoctor={handleBookDoctor}
        />

        <HowItWorks />
      </main>

      <Footer />

      {/* Booking Dialog */}
      <BookingModal
        isOpen={isBookingModalOpen}
        onClose={() => setIsBookingModalOpen(false)}
        doctor={bookingDoctor}
        onBookingSuccess={() => {
          if (user?.role === 'PATIENT') {
            navigate('/patient/appointments');
          }
        }}
      />
    </div>
  );
}
