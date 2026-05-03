import Header from "../../components/instructor/Header.jsx";

export default function Enrollments() {
  return (
    <div>
      <Header
        title="Enrollments"
        description="See who has enrolled in your courses"
      />
      <div className="card-enterprise">
        <div className="card-enterprise-body empty-state-enterprise">
          <h3>Enrollments View</h3>
          <p>This feature will show enrollment data across all your courses</p>
        </div>
      </div>
    </div>
  );
}
