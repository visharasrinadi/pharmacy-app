import React, { useState, useMemo } from "react";

// PharmacyDashboard.jsx
// Single-file React component styled with Tailwind CSS.
// Default export is a functional component ready to drop into a React + Tailwind project.

export default function PharmacyDashboard() {
  // Sample data - replace with API calls
  const initialMedicines = [
    { id: "M001", name: "Paracetamol 500mg", stock: 120, price: 1.5, expiry: "2026-03-01", supplier: "SunPharma" },
    { id: "M002", name: "Amoxicillin 250mg", stock: 48, price: 2.75, expiry: "2025-11-20", supplier: "CureLabs" },
    { id: "M003", name: "Cetirizine 10mg", stock: 0, price: 0.9, expiry: "2027-02-15", supplier: "AllerPharm" },
    { id: "M004", name: "Insulin Rapid", stock: 14, price: 18.0, expiry: "2025-12-05", supplier: "BioLife" },
    { id: "M005", name: "Metformin 500mg", stock: 240, price: 0.7, expiry: "2028-06-30", supplier: "GlucoMed" },
  ];

  const [medicines] = useState(initialMedicines);
  const [query, setQuery] = useState("");
  const [lowStockOnly, setLowStockOnly] = useState(false);
  const [sortBy, setSortBy] = useState("name");
  const [selected, setSelected] = useState(null);

  const lowStockThreshold = 20;

  const stats = useMemo(() => {
    const total = medicines.length;
    const lowStock = medicines.filter((m) => m.stock <= lowStockThreshold).length;
    const outOfStock = medicines.filter((m) => m.stock === 0).length;
    const value = medicines.reduce((acc, m) => acc + m.stock * m.price, 0);
    return { total, lowStock, outOfStock, value };
  }, [medicines]);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    let arr = medicines.filter((m) => {
      if (!q) return true;
      return (
        m.name.toLowerCase().includes(q) ||
        m.id.toLowerCase().includes(q) ||
        (m.supplier || "").toLowerCase().includes(q)
      );
    });
    if (lowStockOnly) arr = arr.filter((m) => m.stock <= lowStockThreshold);

    arr.sort((a, b) => {
      if (sortBy === "name") return a.name.localeCompare(b.name);
      if (sortBy === "stock") return b.stock - a.stock;
      if (sortBy === "expiry") return new Date(a.expiry) - new Date(b.expiry);
      return 0;
    });
    return arr;
  }, [medicines, query, lowStockOnly, sortBy]);

  return (
    <div className="min-h-screen bg-gray-50 text-gray-800">
      <div className="max-w-7xl mx-auto p-4">
        {/* Header */}
        <header className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold">Rx</div>
            <div>
              <h1 className="text-2xl font-semibold">Pharmacy Dashboard</h1>
              <p className="text-sm text-gray-500">Manage stock, orders and patients — all in one place</p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="hidden sm:flex flex-col text-right">
              <span className="text-sm text-gray-500">Logged in as</span>
              <strong>Pharmacist</strong>
            </div>
            <button className="px-3 py-2 bg-white border rounded-md shadow-sm">Settings</button>
            <button className="px-3 py-2 bg-indigo-600 text-white rounded-md shadow-sm">New Order</button>
          </div>
        </header>

        {/* Stats */}
        <section className="grid grid-cols-1 sm:grid-cols-4 gap-4 mb-6">
          <Card title="Total Medicines" value={stats.total} />
          <Card title="Low Stock" value={stats.lowStock} warning />
          <Card title="Out of Stock" value={stats.outOfStock} danger />
          <Card title="Inventory Value" value={`$${stats.value.toFixed(2)}`} />
        </section>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left - Medicines Table */}
          <div className="lg:col-span-2 bg-white rounded-2xl shadow p-4">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  placeholder="Search by name, id or supplier..."
                  className="px-3 py-2 border rounded-md w-72"
                />
                <label className="flex items-center gap-2 text-sm">
                  <input type="checkbox" checked={lowStockOnly} onChange={(e) => setLowStockOnly(e.target.checked)} /> Low stock only
                </label>
              </div>

              <div className="flex items-center gap-3">
                <label className="text-sm text-gray-600">Sort</label>
                <select value={sortBy} onChange={(e) => setSortBy(e.target.value)} className="px-2 py-1 border rounded-md">
                  <option value="name">Name</option>
                  <option value="stock">Stock (high → low)</option>
                  <option value="expiry">Expiry (soonest)</option>
                </select>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="min-w-full table-auto text-sm">
                <thead className="text